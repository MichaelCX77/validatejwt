package com.api.validatejwt.v1.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.api.validatejwt.v1.exception.ClientException;
import com.api.validatejwt.v1.util.ErrorResponse;

@DisplayName("Teste unitário do GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    private ClientException clientException;
    private HttpMessageNotReadableException httpMessageNotReadableException;
    private MethodArgumentNotValidException methodArgumentNotValidException;
    private Exception genericException;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
        MDC.clear();

        clientException = new ClientException(HttpStatus.UNAUTHORIZED, "Token inválido");

        MockHttpInputMessage inputMessage = new MockHttpInputMessage(new byte[0]);
        IOException cause = new IOException("Formato JSON inválido");
        httpMessageNotReadableException = new HttpMessageNotReadableException("Erro de leitura", cause, inputMessage);

        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("dto", "campo", "Campo obrigatório");
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        methodArgumentNotValidException = new MethodArgumentNotValidException(null, bindingResult);

        genericException = new RuntimeException("Erro desconhecido");
    }

    @Nested
    @DisplayName("handleClientException")
    class HandleClientException {

        @Test
        @DisplayName("Deve tratar ClientException e retornar status correto e mensagem esperada")
        void deveTratarClientException() {
            ResponseEntity<ErrorResponse> response = handler.handleClientException(clientException);

            assertAll("Validações ClientException",
                () -> assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(401, response.getBody().getStatus()),
                () -> assertEquals("Token inválido", response.getBody().getMessage()),
                () -> assertEquals("401", MDC.get("status"))
            );
        }
    }

    @Nested
    @DisplayName("handleMessageNotReadableException")
    class HandleMessageNotReadableException {

        @Test
        @DisplayName("Deve tratar HttpMessageNotReadableException e retornar status 400 com mensagem correta")
        void deveTratarHttpMessageNotReadableException() {
            ResponseEntity<ErrorResponse> response = handler.handleMessageNotReadableException(httpMessageNotReadableException);

            assertAll("Validações HttpMessageNotReadableException",
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(400, response.getBody().getStatus()),
                () -> assertEquals("Erro de leitura", response.getBody().getMessage()),
                () -> assertEquals("400", MDC.get("status"))
            );
        }
    }

    @Nested
    @DisplayName("handleValidationException")
    class HandleValidationException {

        @Test
        @DisplayName("Deve tratar MethodArgumentNotValidException e agregar mensagem do campo")
        void deveTratarMethodArgumentNotValidException() {
            ResponseEntity<ErrorResponse> response = handler.handleValidationException(methodArgumentNotValidException);

            assertAll("Validações MethodArgumentNotValidException",
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(400, response.getBody().getStatus()),
                () -> assertEquals("Campo obrigatório", response.getBody().getMessage()),
                () -> assertEquals("400", MDC.get("status"))
            );
        }
    }

    @Nested
    @DisplayName("handleGenericException")
    class HandleGenericException {

        @Test
        @DisplayName("Deve tratar exceção genérica retornando status 500 com mensagem padrão")
        void deveTratarExceptionGenerica() {
            ResponseEntity<ErrorResponse> response = handler.handleGenericException(genericException);

            assertAll("Validações Exception Genérica",
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(500, response.getBody().getStatus()),
                () -> assertEquals("Erro interno, procure a equipe de suporte", response.getBody().getMessage()),
                () -> assertEquals("500", MDC.get("status"))
            );
        }
    }

    @Nested
    @DisplayName("handleNoHandlerFoundException")
    class HandleNoHandlerFoundException {

        @Test
        @DisplayName("Deve tratar NoHandlerFoundException e retornar status 404 com mensagem apropriada")
        void deveTratarNoHandlerFoundException() {
            NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/rota-inexistente", null);

            ResponseEntity<ErrorResponse> response = handler.handleNoHandlerFoundException(ex);

            assertAll("Validações NoHandlerFoundException",
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(404, response.getBody().getStatus()),
                () -> assertEquals("Recurso não encontrado: /rota-inexistente", response.getBody().getMessage()),
                () -> assertEquals("404", MDC.get("status"))
            );
        }
    }
}
