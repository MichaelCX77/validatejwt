package com.api.validatejwt.v1.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.api.validatejwt.v1.exceptions.ClientException;
import com.api.validatejwt.v1.util.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ClientException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(ClientException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getHttpStatus().value(),
            "Erro de violação de integridade de dados: " + ex.getMessage(),
            System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }
    
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(RuntimeException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro interno, procure a equipe de suporte",
            System.currentTimeMillis()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
