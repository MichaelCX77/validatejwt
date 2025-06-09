package com.api.validatejwt.v1.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste unitário da classe Jwt")
class JwtTest {

    private Validator validator;

    private static final String VALID_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Quando criar um Jwt válido")
    class JwtValido {

        private Jwt jwt;

        @BeforeEach
        void init() {
            jwt = new Jwt(VALID_JWT);
        }

        @Test
        @DisplayName("Deve criar objeto Jwt com campo jwt preenchido corretamente")
        void deveCriarObjetoComJwtPreenchido() {
            assertNotNull(jwt);
            assertEquals(VALID_JWT, jwt.getJwt());
        }

        @Test
        @DisplayName("Deve passar na validação de Bean Validation")
        void devePassarValidacao() {
            Set<ConstraintViolation<Jwt>> violations = validator.validate(jwt);
            assertTrue(violations.isEmpty(), "Não deve haver violações para jwt válido");
        }

        @Test
        @DisplayName("Método logInfo() deve executar sem lançar exceção")
        void logInfoNaoDeveLancarExcecao() {
            assertDoesNotThrow(jwt::logInfo);
        }
    }

    @Nested
    @DisplayName("Quando criar um Jwt inválido")
    class JwtInvalido {

        @Test
        @DisplayName("Deve falhar na validação quando jwt estiver em branco")
        void deveFalharValidacaoSeJwtBranco() {
            Jwt jwt = new Jwt("");
            Set<ConstraintViolation<Jwt>> violations = validator.validate(jwt);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("jwt")));
        }

        @Test
        @DisplayName("Deve falhar na validação quando jwt for nulo")
        void deveFalharValidacaoSeJwtNulo() {
            Jwt jwt = new Jwt(null);
            Set<ConstraintViolation<Jwt>> violations = validator.validate(jwt);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("jwt")));
        }
    }

    @Nested
    @DisplayName("Quando usar construtor vazio e setters")
    class ConstrutorVazio {

        @Test
        @DisplayName("Deve permitir setar campo jwt e retornar valor correto")
        void devePermitirSetarJwt() {
            Jwt jwt = new Jwt();
            jwt.setJwt(VALID_JWT);
            assertEquals(VALID_JWT, jwt.getJwt());
        }
    }
}
