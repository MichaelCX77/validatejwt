package com.api.validatejwt.v1.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.api.validatejwt.v1.enums.EnumRole;
import com.api.validatejwt.v1.exceptions.ClientException;
import com.api.validatejwt.v1.model.Claims;
import com.api.validatejwt.v1.model.Jwt;
import com.api.validatejwt.v1.model.JwtDTO;
import com.api.validatejwt.v1.util.ReflectionUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class JwtService {

    private static final String JWT_INVALID_MSG = "JWT inválido";

    private final Validator validator;

    public JwtService(Validator validator) {
        this.validator = validator;
    }

    public JwtDTO validate(Jwt jwtObj) {
        Claims claims = extractAndValidateClaims(jwtObj);

        // Validação automática dos campos com @NotBlank
        validateBean(claims);

        // Validações manuais complementares
        validateName(claims.getName());
        validateSeed(claims.getSeed());
        validateRole(claims.getRole());

        return new JwtDTO(true);
    }

    private Claims extractAndValidateClaims(Jwt jwt) {
        String payloadJson = extractPayload(jwt.getJwt());
        return parseClaims(payloadJson);
    }

    private String extractPayload(String jwt) {
        String[] parts = jwt.split("\\.");
        if (parts.length != 3) {
            throw new ClientException(HttpStatus.OK, JWT_INVALID_MSG);
        }
        try {
            return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new ClientException(HttpStatus.OK, JWT_INVALID_MSG);
        }
    }

    private Claims parseClaims(String payloadJson) {
        try {
            return new ObjectMapper().readValue(payloadJson, Claims.class);
        } catch (UnrecognizedPropertyException e) {
            String allowedFields = Arrays.toString(getFieldNames(Claims.class));
            throw new ClientException(
                HttpStatus.OK,
                "Claim inválido: " + e.getPropertyName() + " / Campos permitidos: " + allowedFields
            );
        } catch (JsonParseException e) {
            throw new ClientException(HttpStatus.OK, JWT_INVALID_MSG);
        } catch (Exception e) {
            throw new RuntimeException("Erro desconhecido: procure a equipe de suporte.", e);
        }
    }

    private void validateBean(Claims claims) {
        Set<ConstraintViolation<Claims>> violations = validator.validate(claims);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                .map(v -> "Campo '" + v.getPropertyPath() + "' informado nos claims é inválido: " + v.getMessage())
                .findFirst()
                .orElse("Erro de validação");
            throw new ClientException(HttpStatus.OK, msg);
        }
    }

    private void validateName(String name) {
        if (name != null) {
            if (name.matches(".*\\d.*")) {
                throw new ClientException(HttpStatus.OK, "Claim inválido: 'Name' não pode conter números");
            }
            if (name.length() > 256) {
                throw new ClientException(HttpStatus.OK, "Claim inválido: 'Name' não pode exceder 256 caracteres");
            }
        }
    }

    private void validateSeed(String seedValue) {
        if (seedValue != null && !isPrime(seedValue)) {
            throw new ClientException(HttpStatus.OK, "Claim inválido: 'Seed' deve ser um número primo");
        }
    }

    private void validateRole(String role) {
        if (!EnumRole.isValidRole(role)) {
            throw new ClientException(
                HttpStatus.OK,
                "Role inválida: " + role + " / Roles disponíveis: " + EnumRole.availableRoles()
            );
        }
    }

    private boolean isPrime(String seedValue) {
        try {
            long number = Long.parseLong(seedValue);
            if (number < 2) return false;
            for (long i = 2; i <= Math.sqrt(number); i++) {
                if (number % i == 0) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String[] getFieldNames(Class<?> clazz) {
        return ReflectionUtils.getFieldNames(clazz);
    }
}
