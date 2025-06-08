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

@RestControllerAdvice
public class GlobalExceptionHandler {

//  Service Validators
	@ExceptionHandler(ClientException.class)
	public ResponseEntity<JwtDTO> handleClientException(ClientException ex) {
		JwtDTO response = new JwtDTO(false, ex.getMessage());
		return ResponseEntity.status(ex.getHttpStatus()).body(response);
	}

//  Validate payload errors
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
	    String message = "Erro ao processar JSON";

	    Throwable cause = ex.getCause();
	    if (cause != null && cause.getClass().getSimpleName().equals("UnrecognizedPropertyException")) {
	        com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException unrecEx = 
	            (com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException) cause;
	        
	        String invalidField = unrecEx.getPropertyName();
	        String allowedFields = unrecEx.getKnownPropertyIds().toString();
	        message = String.format("Campo inválido no JSON: \"%s\". Campos permitidos são: %s", invalidField, allowedFields);
	    }

	    ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
	    return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
		List<String> messages = ex.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> fieldError.getDefaultMessage()).collect(Collectors.toList());

		String msg = String.join("; ", messages);

		ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), msg);
		return ResponseEntity.badRequest().body(error);
	}

	// Unespected errors
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Erro interno, procure a equipe de suporte");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

	// Unespected errors
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Erro interno, procure a equipe de suporte");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}

}
