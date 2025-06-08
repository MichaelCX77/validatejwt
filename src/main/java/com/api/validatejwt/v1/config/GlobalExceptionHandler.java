package com.api.validatejwt.v1.config;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.api.validatejwt.v1.exceptions.ClientException;
import com.api.validatejwt.v1.util.ErrorResponse;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(ClientException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getHttpStatus().value(),
            "Erro de violação de integridade de dados: " + ex.getMessage()
        );
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }
    
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno, procure a equipe de suporte"
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> mensagens = ex.getBindingResult()
                                  .getFieldErrors()
                                  .stream()
                                  .map(fieldError -> fieldError.getDefaultMessage())
                                  .collect(Collectors.toList());

        String msg = String.join("; ", mensagens);

        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            msg
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleValidationException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof UnrecognizedPropertyException) {
            UnrecognizedPropertyException unrecognizedEx = (UnrecognizedPropertyException) cause;

            String campo = unrecognizedEx.getPropertyName();
            String msg = "Campo não permitido: " + campo;

            ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                msg
            );
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        ErrorResponse fallback = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "JSON mal formatado ou inválido",
            System.currentTimeMillis(),
            MDC.get("requestId")
        );
        return new ResponseEntity<>(fallback, HttpStatus.BAD_REQUEST);
    }
        
}
