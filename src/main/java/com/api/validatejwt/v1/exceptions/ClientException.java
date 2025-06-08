package com.api.validatejwt.v1.exceptions;

import org.springframework.http.HttpStatus;

public class ClientException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final HttpStatus httpStatus;

	public ClientException(HttpStatus httpStatus, String message) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
