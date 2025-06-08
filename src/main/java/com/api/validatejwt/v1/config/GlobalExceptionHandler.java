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
 * Classe responsável por capturar e tratar exceções lançadas nos controllers da aplicação.
 * Centraliza a lógica de tratamento de erros para manter os controllers limpos
 * e padronizar respostas de erro com estrutura consistente.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String GENERIC_ERROR_MSG = "Erro interno, procure a equipe de suporte";
    private static final String JSON_INVALID_MSG = "JSON inválido ou malformado.";

    /**
     * Trata exceções personalizadas do tipo {@link ClientException}, geralmente associadas a regras de negócio.
     *
     * @param ex instância da exceção personalizada
     * @return resposta HTTP com status e mensagem da exceção
     */
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> handleClientException(ClientException ex) {
        log.warn("status={} message={}", ex.getHttpStatus(), ex.getMessage());
        ErrorResponse error = new ErrorResponse(ex.getHttpStatus().value(), ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    /**
     * Trata exceções quando o corpo JSON da requisição está malformado ou contém campos inesperados.
     *
     * @param ex exceção capturada ao tentar desserializar o JSON
     * @return resposta HTTP 400 com descrição do problema no JSON
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = buildJsonErrorMessage(ex);
        log.warn("Erro de leitura de JSON: {}", message);
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Constrói uma mensagem mais clara para erros relacionados à leitura e parsing de JSON.
     *
     * @param ex exceção original de leitura do JSON
     * @return mensagem amigável explicando o erro
     */
    private String buildJsonErrorMessage(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof UnrecognizedPropertyException unrecEx) {
            String invalidField = unrecEx.getPropertyName();
            String allowedFields = unrecEx.getKnownPropertyIds().toString();
            return String.format("Campo inválido no JSON: \"%s\". Campos permitidos: %s", invalidField, allowedFields);
        }

        return JSON_INVALID_MSG;
    }

    /**
     * Trata exceções lançadas por validações de campos em DTOs (ex: @NotBlank, @Size).
     * Agrupa todas as mensagens de erro em uma resposta única.
     *
     * @param ex exceção de validação de argumento do método
     * @return resposta HTTP 400 com mensagens de erro concatenadas
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        String msg = String.join("; ", messages);
        log.warn("Erros de validação: {}", msg);
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), msg);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Fallback para qualquer outra exceção não tratada explicitamente.
     * Garante que o sistema não exponha detalhes técnicos ao cliente final.
     *
     * @param ex exceção genérica
     * @return resposta HTTP 500 com mensagem genérica de erro
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Erro inesperado capturado", ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                GENERIC_ERROR_MSG
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
