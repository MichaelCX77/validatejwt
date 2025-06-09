package com.api.validatejwt.v1.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Teste unitário da classe JwtDTO")
class JwtDTOTest {

    private JwtDTO jwtDTO;

    private static final Boolean VALID_TRUE = true;
    private static final Boolean VALID_FALSE = false;
    private static final String SUCCESS_MESSAGE = "Validado com sucesso";
    private static final String ERROR_MESSAGE = "Token inválido";

    @BeforeEach
    void setup() {
        jwtDTO = new JwtDTO();
    }

    @Nested
    @DisplayName("Testes para construtores e getters/setters")
    class ConstrutoresEGetsSets {

        @Test
        @DisplayName("Deve criar JwtDTO vazio com valores nulos")
        void deveCriarJwtDTOVazio() {
            assertNull(jwtDTO.getIsValid());
            assertNull(jwtDTO.getMessage());
        }

        @Test
        @DisplayName("Deve criar JwtDTO com construtor isValid e mensagem padrão")
        void deveCriarJwtDTOComIsValid() {
            JwtDTO dto = new JwtDTO(VALID_TRUE);

            assertEquals(VALID_TRUE, dto.getIsValid());
            assertEquals(SUCCESS_MESSAGE, dto.getMessage());
        }

        @Test
        @DisplayName("Deve permitir setar e obter valores de isValid e message")
        void deveSetarEObterValores() {
            jwtDTO.setIsValid(VALID_FALSE);
            jwtDTO.setMessage(ERROR_MESSAGE);

            assertEquals(VALID_FALSE, jwtDTO.getIsValid());
            assertEquals(ERROR_MESSAGE, jwtDTO.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes para método logValidationError")
    class LogValidationErrorTests {

        @Test
        @DisplayName("Deve logar erro com mensagem sem exceção")
        void deveLogarErroSemExcecao() {
            assertDoesNotThrow(() -> jwtDTO.logValidationError(ERROR_MESSAGE, null));
        }

        @Test
        @DisplayName("Deve logar erro com mensagem e exceção")
        void deveLogarErroComExcecao() {
            Exception ex = new IllegalArgumentException("Exceção de teste");
            assertDoesNotThrow(() -> jwtDTO.logValidationError(ERROR_MESSAGE, ex));
        }
    }
}
