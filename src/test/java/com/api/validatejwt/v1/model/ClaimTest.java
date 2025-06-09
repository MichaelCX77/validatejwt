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

@DisplayName("Teste unitário da classe Claim")
class ClaimTest {

    private Validator validator;

    private static final String VALID_ROLE = "ADMIN";
    private static final String VALID_SEED = "someSeed";
    private static final String VALID_NAME = "userName";

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Quando criar um Claim com dados válidos")
    class CriacaoValida {

        private Claim claim;

        @BeforeEach
        void init() {
            claim = new Claim(VALID_ROLE, VALID_SEED, VALID_NAME);
        }

        @Test
        @DisplayName("Deve criar o objeto corretamente e ter os campos esperados")
        void deveCriarObjetoCorretamente() {
            assertNotNull(claim);
            assertEquals(VALID_ROLE, claim.getRole());
            assertEquals(VALID_SEED, claim.getSeed());
            assertEquals(VALID_NAME, claim.getName());
        }

        @Test
        @DisplayName("Deve passar na validação de Bean Validation")
        void devePassarValidacao() {
            Set<ConstraintViolation<Claim>> violations = validator.validate(claim);
            assertTrue(violations.isEmpty(), "Não deve haver violações para dados válidos");
        }

        @Test
        @DisplayName("Método logInfo() deve executar sem lançar exceções")
        void logInfoNaoDeveLancarExcecao() {
            assertDoesNotThrow(claim::logInfo);
        }
    }

    @Nested
    @DisplayName("Quando criar um Claim com dados inválidos (brancos ou nulos)")
    class CriacaoInvalida {

        @Test
        @DisplayName("Deve falhar na validação quando role estiver em branco")
        void deveFalharSeRoleBranco() {
            Claim claim = new Claim("", VALID_SEED, VALID_NAME);
            Set<ConstraintViolation<Claim>> violations = validator.validate(claim);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("role")));
        }

        @Test
        @DisplayName("Deve falhar na validação quando seed estiver em branco")
        void deveFalharSeSeedBranco() {
            Claim claim = new Claim(VALID_ROLE, " ", VALID_NAME);
            Set<ConstraintViolation<Claim>> violations = validator.validate(claim);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("seed")));
        }

        @Test
        @DisplayName("Deve falhar na validação quando name estiver em branco")
        void deveFalharSeNameBranco() {
            Claim claim = new Claim(VALID_ROLE, VALID_SEED, "");
            Set<ConstraintViolation<Claim>> violations = validator.validate(claim);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        }
    }

    @Nested
    @DisplayName("Quando usar o construtor vazio")
    class ConstrutorVazio {

        @Test
        @DisplayName("Deve permitir setar campos e retornar valores corretos")
        void devePermitirSettersGetters() {
            Claim claim = new Claim();
            claim.setRole(VALID_ROLE);
            claim.setSeed(VALID_SEED);
            claim.setName(VALID_NAME);

            assertEquals(VALID_ROLE, claim.getRole());
            assertEquals(VALID_SEED, claim.getSeed());
            assertEquals(VALID_NAME, claim.getName());
        }
    }
}
