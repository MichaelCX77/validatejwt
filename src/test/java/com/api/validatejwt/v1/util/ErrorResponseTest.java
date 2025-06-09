package com.api.validatejwt.v1.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste unitário do ErrorResponse")
class ErrorResponseTest {

    private ErrorResponse errorResponse;

    private final int STATUS_CODE = 400;
    private final String MESSAGE = "Requisição inválida";

    @BeforeEach
    void setup() {
        errorResponse = new ErrorResponse(STATUS_CODE, MESSAGE);
    }

    @Nested
    @DisplayName("Validações de campos e construtor")
    class ValidacoesCampos {

        @Test
        @DisplayName("Deve inicializar corretamente com valores fornecidos")
        void deveInicializarCorretamente() {
            assertEquals(STATUS_CODE, errorResponse.getStatus());
            assertEquals(MESSAGE, errorResponse.getMessage());
        }

        @Test
        @DisplayName("Deve permitir atualização dos campos via setters")
        void deveAtualizarCamposComSetters() {
            int novoStatus = 401;
            String novaMensagem = "Não autorizado";

            errorResponse.setStatus(novoStatus);
            errorResponse.setMessage(novaMensagem);

            assertEquals(novoStatus, errorResponse.getStatus());
            assertEquals(novaMensagem, errorResponse.getMessage());
        }

        @Test
        @DisplayName("Deve ter método toString não nulo e com os valores corretos")
        void deveConterToStringCorreto() {
            String toString = errorResponse.toString();
            assertNotNull(toString);
            assertTrue(toString.contains(String.valueOf(STATUS_CODE)));
            assertTrue(toString.contains(MESSAGE));
        }

        @Test
        @DisplayName("Deve validar igualdade entre dois objetos com os mesmos dados")
        void deveValidarEqualsECodigoHash() {
            ErrorResponse outroErro = new ErrorResponse(STATUS_CODE, MESSAGE);

            assertEquals(errorResponse, outroErro);
            assertEquals(errorResponse.hashCode(), outroErro.hashCode());
        }
    }
}
