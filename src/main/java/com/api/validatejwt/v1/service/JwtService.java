package com.api.validatejwt.v1.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

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

import jakarta.validation.ValidationException;

@Service
public class JwtService {

    public JwtDTO validate(Jwt jwtObj) {
        Claims claims = extractAndValidateClaims(jwtObj);

        validateName(claims.getName());
        validateSeed(claims.getSeed());
        validateRole(claims.getRole());

        jwtObj.setClaims(claims);
        return new JwtDTO(true);
    }

    private Claims extractAndValidateClaims(Jwt jwt) {
        try {
            String[] parts = jwt.getJwtToken().split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("JWT inválido");
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            return new ObjectMapper().readValue(payloadJson, Claims.class);

        } catch (UnrecognizedPropertyException e) {
            String allowedFields = Arrays.toString(getFieldNames(Claims.class));
            throw new ClientException(
                HttpStatus.OK,
                "Claim inválido: " + e.getPropertyName() + " / Campos permitidos: " + allowedFields
            );

        } catch (JsonParseException e) {
            throw new ClientException(HttpStatus.OK, "JWT inválido");

        } catch (IllegalArgumentException | ValidationException e) {
            throw new ClientException(HttpStatus.OK, e.getMessage());

        } catch (Exception e) {
            throw new RuntimeException("Erro desconhecido: procure a equipe de suporte.", e);
        }
    }

    private void validateName(String name) {
        if (name != null && name.matches(".*\\d.*")) {
            throw new ClientException(HttpStatus.OK, "Claim inválido: 'Name' não pode conter números");
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
