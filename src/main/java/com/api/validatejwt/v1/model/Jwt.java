package com.api.validatejwt.v1.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.extern.slf4j.Slf4j;

/**
 * Representa um token JWT recebido ou manipulado pela aplicação.
 * Implementa {@link Serializable} para possibilitar transferência ou armazenamento.
 * 
 * A propriedade {@code jwt} é obrigatória e representa o token JWT em formato String.
 * 
 * A anotação {@code @JsonIgnoreProperties(ignoreUnknown = false)} garante que
 * qualquer campo desconhecido na desserialização gere erro, evitando dados inesperados.
 */
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Jwt implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Token JWT em formato string.
     * Campo obrigatório para autenticação/autorização.
     */
    @NotBlank(message = "O campo jwt é obrigatório")
    private String jwt;

    /**
     * Loga informação para auditoria/debug da criação do objeto Jwt.
     * Deve ser chamado explicitamente para evitar poluição dos logs.
     */
    public void logInfo() {
        log.debug("Objeto Jwt criado com token de tamanho {}", jwt != null ? jwt.length() : 0);
    }
}
