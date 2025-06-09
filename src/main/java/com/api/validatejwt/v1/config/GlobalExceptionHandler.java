package com.api.validatejwt.v1.config;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.api.validatejwt.v1.exception.ClientException;
import com.api.validatejwt.v1.util.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Handler global para interceptação e tratamento centralizado de exceções lançadas pelos controllers.
 * Aplica padronização de respostas de erro e log estruturado com MDC para rastreamento.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String GENERIC_ERROR_MSG = "Erro interno, procure a equipe de suporte";

    /**
     * Trata exceções do tipo {@link ClientException}, que representam erros de negócio definidos pela aplicação.
     *
     * @param ex Exceção personalizada lançada por regras de negócio
     * @return {@link ResponseEntity} com status e mensagem apropriados
     */
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> handleClientException(ClientException ex) {
        int status = ex.getHttpStatus().value();
        MDC.put("status", String.valueOf(status));
        log.warn(ex.getMessage());
        ErrorResponse error = new ErrorResponse(status, ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    /**
     * Trata exceções relacionadas à desserialização incorreta do JSON da requisição.
     * Isso pode incluir campos inesperados, formatos inválidos ou estrutura malformada.
     *
     * @param ex Exceção lançada pela falha na leitura do corpo da requisição
     * @return {@link ResponseEntity} com detalhes amigáveis do erro
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        int status = HttpStatus.BAD_REQUEST.value();
        MDC.put("status", String.valueOf(status));
        log.warn("Falha ao interpretar corpo da requisição: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(status, ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }


    /**
     * Trata exceções de validação de campos de entrada nos DTOs,
     * como anotações do tipo {@code @NotBlank}, {@code @Size}, etc.
     *
     * @param ex Exceção de validação
     * @return {@link ResponseEntity} com lista de mensagens de erro
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        int status = HttpStatus.BAD_REQUEST.value();
        MDC.put("status", String.valueOf(status));

        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        String combinedMessage = String.join("; ", messages);
        log.warn("Erro de validação nos campos da requisição: {}", combinedMessage);

        ErrorResponse error = new ErrorResponse(status, combinedMessage);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Fallback para exceções não mapeadas especificamente.
     * Evita exposição de detalhes internos da aplicação ao cliente.
     *
     * @param ex Exceção genérica ou não tratada
     * @return {@link ResponseEntity} com mensagem genérica e status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        MDC.put("status", String.valueOf(status));

        log.error("Falha inesperada no servidor. Traceback registrado.", ex);

        ErrorResponse error = new ErrorResponse(status, GENERIC_ERROR_MSG);
        return ResponseEntity.status(status).body(error);
    }
}
