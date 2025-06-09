package com.api.validatejwt.v1.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.api.validatejwt.v1.enums.EnumRole;
import com.api.validatejwt.v1.exception.ClientException;
import com.api.validatejwt.v1.model.Claim;
import com.api.validatejwt.v1.model.Jwt;
import com.api.validatejwt.v1.model.JwtDTO;
import com.api.validatejwt.v1.util.ReflectionUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

/**
 * Serviço responsável pela validação de tokens JWT.
 * Realiza extração, validação estrutural e semântica dos claims do token.
 * Aplica validações customizadas e lança exceções específicas em caso de erro.
 */
@Service
public class JwtService {

    private static final String JWT_INVALID_MSG = "JWT inválido";

    private final Validator validator;
    private final ObjectMapper objectMapper;

    /**
     * Construtor que recebe o validador e instancia o ObjectMapper.
     * 
     * @param validator Validador de bean para validar claims com anotações
     */
    public JwtService(Validator validator) {
        this.validator = validator;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Valida o JWT fornecido. Extrai e valida os claims do token,
     * incluindo validação automática e regras customizadas.
     * 
     * @param jwtObj Objeto contendo o JWT a ser validado
     * @return {@link JwtDTO} indicando sucesso da validação
     * @throws ClientException em caso de validação inválida
     */
    public JwtDTO validate(Jwt jwtObj) {

        Claim claims = extractAndValidateClaims(jwtObj);

        validateBean(claims);

        validateName(claims.getName());
        validateSeed(claims.getSeed());
        validateRole(claims.getRole());

        return new JwtDTO(true);
    }

    /**
     * Extrai o payload do JWT e faz o parse dos claims em objeto Claims.
     * 
     * @param jwt Objeto JWT contendo o token
     * @return Claims extraídos e validados
     * @throws ClientException para token mal formado ou claims inválidos
     */
    private Claim extractAndValidateClaims(Jwt jwt) {
        String payloadJson = extractPayload(jwt.getJwt());
        return parseClaims(payloadJson);
    }

    /**
     * Extrai a parte do payload do JWT decodificando Base64 URL.
     * 
     * @param jwtString String do token JWT
     * @return JSON do payload decodificado
     * @throws ClientException se token estiver mal formatado ou inválido
     */
    private String extractPayload(String jwtString) {
        String[] parts = jwtString.split("\\.");
        if (parts.length != 3) {
            throw new ClientException(HttpStatus.OK, JWT_INVALID_MSG);
        }
        try {
            return new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
        	
            throw new ClientException(HttpStatus.OK, JWT_INVALID_MSG);
        }
    }

    /**
     * Converte JSON do payload em objeto Claims, tratando exceções específicas.
     * 
     * @param payloadJson JSON do payload JWT
     * @return Objeto Claims convertido
     * @throws ClientException para propriedades inválidas ou JSON mal formado
     */
    private Claim parseClaims(String payloadJson) {
        try {
            return objectMapper.readValue(payloadJson, Claim.class);
        } catch (UnrecognizedPropertyException e) {
            String allowedFields = Arrays.toString(getFieldNames(Claim.class));
            String msg = String.format("Campo inválido no JSON: %s / Campos permitidos: %s", e.getPropertyName(), allowedFields);
            throw new ClientException(HttpStatus.OK, msg);
        } catch (JsonParseException e) {
            throw new ClientException(HttpStatus.OK, JWT_INVALID_MSG);
        } catch (Exception e) {
            throw new RuntimeException("Erro desconhecido: procure a equipe de suporte.", e);
        }
    }

    /**
     * Valida o objeto Claims usando validação Bean Validation (@NotBlank, etc).
     * 
     * @param claims Claims a serem validados
     * @throws ClientException se houver violação de restrições
     */
    private void validateBean(Claim claims) {
        Set<ConstraintViolation<Claim>> violations = validator.validate(claims);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                .map(v -> "Campo '" + v.getPropertyPath() + "' informado nos claims é inválido: " + v.getMessage())
                .findFirst()
                .orElse("Erro de validação");
            throw new ClientException(HttpStatus.OK, msg);
        }
    }

    /**
     * Valida o campo 'name' dos claims com regras específicas.
     * 
     * @param name Nome a ser validado
     * @throws ClientException se o nome contiver números ou exceder tamanho
     */
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

    /**
     * Valida o campo 'role' verificando se está entre os papéis válidos.
     * 
     * @param role Papel a ser validado
     * @throws ClientException se o papel for inválido
     */
    private void validateRole(String role) {
        if (!EnumRole.isValidRole(role)) {
            String msg = "Role inválida: " + role + " / Roles disponíveis: " + EnumRole.availableRoles();
            throw new ClientException(HttpStatus.OK, msg);
        }
    }

    /**
     * Valida o campo 'seed' garantindo que seja um número primo válido.
     *
     * @param seedValue Valor da seed em string
     * @throws ClientException se não for um número válido ou não for primo
     */
    private void validateSeed(String seedValue) {
        if (seedValue == null) return;

        try {
            long number = Long.parseLong(seedValue);
            if (number < 2) {
                throw new ClientException(HttpStatus.OK, "Claim inválido: 'Seed' deve ser um número primo");
            }
            for (long i = 2; i <= Math.sqrt(number); i++) {
                if (number % i == 0) {
                    throw new ClientException(HttpStatus.OK, "Claim inválido: 'Seed' deve ser um número primo");
                }
            }
        } catch (NumberFormatException e) {
            throw new ClientException(HttpStatus.OK, "Claim inválido: 'Seed' deve ser um número primo");
        }
    }


    /**
     * Recupera os nomes dos campos da classe para auxiliar em mensagens de erro.
     * 
     * @param clazz Classe a ser analisada
     * @return Array de nomes de campos
     */
    private String[] getFieldNames(Class<?> clazz) {
        return ReflectionUtils.getFieldNames(clazz);
    }
}
