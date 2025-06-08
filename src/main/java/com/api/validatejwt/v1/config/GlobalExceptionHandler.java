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
import com.api.validatejwt.v1.model.JwtDTO;
import com.api.validatejwt.v1.util.ErrorResponse;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	
//  Service Validators
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<JwtDTO> handleClientException(ClientException ex) {
        JwtDTO response = new JwtDTO(false, ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof UnrecognizedPropertyException) {
            JwtDTO response = new JwtDTO(false, ex.getMessage());
            return ResponseEntity.ok().body(response);
        }

        JwtDTO response = new JwtDTO(false, ex.getMessage());
        return ResponseEntity.ok().body(response);
    }

//  Validate payload errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
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
        return ResponseEntity.badRequest().body(error);
    }

	//  Unespected errors
	  @ExceptionHandler(RuntimeException.class)
	  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
	      ErrorResponse error = new ErrorResponse(
	          HttpStatus.INTERNAL_SERVER_ERROR.value(),
	          "Erro interno, procure a equipe de suporte"
	      );
	      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	  }
	  
	//  Unespected errors
	  @ExceptionHandler(Exception.class)
	  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
	      ErrorResponse error = new ErrorResponse(
	          HttpStatus.INTERNAL_SERVER_ERROR.value(),
	          "Erro interno, procure a equipe de suporte"
	      );
	      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	  }

}
