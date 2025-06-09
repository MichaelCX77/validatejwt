package com.api.validatejwt.v1.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.api.validatejwt.v1.exception.ClientException;

@DisplayName("Teste unitário da ClientException")
class ClientExceptionTest {

    private HttpStatus mockStatus;
    private String mockMessage;

    @BeforeEach
    void setup() {
        mockStatus = HttpStatus.BAD_REQUEST;
        mockMessage = "Erro de negócio simulado";
    }

    @Nested
    @DisplayName("Quando criar uma ClientException")
    class CriacaoException {

        @Test
        @DisplayName("Deve armazenar corretamente status e mensagem")
        void deveArmazenarStatusEMensagem() {
            ClientException exception = new ClientException(mockStatus, mockMessage);

            assertEquals(mockStatus, exception.getHttpStatus(), "O status HTTP deve ser o esperado");
            assertEquals(mockMessage, exception.getMessage(), "A mensagem da exceção deve ser a esperada");
        }

        @Test
        @DisplayName("Deve herdar RuntimeException")
        void deveSerRuntimeException() {
            ClientException exception = new ClientException(mockStatus, mockMessage);
            assertTrue(exception instanceof RuntimeException, "Deve ser uma RuntimeException");
        }
    }
}
