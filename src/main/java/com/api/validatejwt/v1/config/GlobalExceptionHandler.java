package com.api.validatejwt.v1.config;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.api.validatejwt.v1.exceptions.ClientException;
import com.api.validatejwt.v1.util.ErrorResponse;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import lombok.extern.slf4j.Slf4j;

/**
 * Classe responsável por capturar e tratar exceções de forma global
 * para todos os controllers da aplicação.
 * As respostas de erro são padronizadas com um corpo de resposta consistente.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String GENERIC_ERROR_MSG = "Erro interno, procure a equipe de suporte";
    private static final String JSON_INVALID_MSG = "JSON Inválido";

    /**
     * Trata exceções do tipo ClientException (exceções customizadas da aplicação)
     * e retorna uma resposta com o status e a mensagem definida pela exceção.
     */
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> handleClientException(ClientException ex) {
        log.warn("ClientException: status={} message={}", ex.getHttpStatus(), ex.getMessage());
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus().value(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    /**
     * Trata exceções quando o corpo da requisição JSON está malformado ou inválido.
     * Ex: campo extra, estrutura incorreta, tipo de dado errado etc.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = buildJsonErrorMessage(ex);
        log.warn("JSON parse error: {}", message);
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Extrai uma mensagem mais específica sobre erro de JSON,
     * como campos desconhecidos ou estrutura inesperada.
     */
    private String buildJsonErrorMessage(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        // Verifica se o erro foi causado por um campo inesperado no JSON
        if (cause instanceof UnrecognizedPropertyException unrecEx) {
            String invalidField = unrecEx.getPropertyName(); // Campo não reconhecido
            String allowedFields = unrecEx.getKnownPropertyIds().toString(); // Campos válidos esperados
            return String.format("Campo inválido no JSON: \"%s\". Campos permitidos são: %s", invalidField, allowedFields);
        }

        return JSON_INVALID_MSG;
    }

    /**
     * Trata exceções geradas por validações de campos com anotações como @NotBlank, @Email etc.
     * Retorna uma lista de mensagens de erro combinadas.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        // Coleta todas as mensagens de erro de validação dos campos do DTO
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage()) // Mensagem definida no DTO
                .collect(Collectors.toList());

        String msg = String.join("; ", messages); // Junta todas as mensagens em uma única string
        log.warn("Validation errors: {}", msg);

        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), msg);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Trata qualquer exceção não prevista (fallback).
     * Garante que o sistema não exponha internamente erros para o usuário final.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro inesperado", ex); // Log completo da exceção para depuração
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), GENERIC_ERROR_MSG);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
