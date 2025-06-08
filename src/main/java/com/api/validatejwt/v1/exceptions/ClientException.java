package com.api.validatejwt.v1.exceptions;

import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * Exceção personalizada para representar erros de negócio da aplicação.
 * Permite encapsular um status HTTP junto com a mensagem do erro.
 */
@Slf4j
public class ClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Status HTTP associado à exceção, para controle do código de resposta.
     */
    private final HttpStatus httpStatus;

    /**
     * Construtor que cria uma exceção de negócio com status HTTP e mensagem personalizada.
     *
     * @param httpStatus Status HTTP que será retornado na resposta
     * @param message Mensagem descritiva do erro
     */
    public ClientException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        log.debug("ClientException criada com status {} e mensagem '{}'", httpStatus.value(), message);
    }

    /**
     * Retorna o status HTTP associado à exceção.
     *
     * @return Status HTTP da exceção
     */
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
