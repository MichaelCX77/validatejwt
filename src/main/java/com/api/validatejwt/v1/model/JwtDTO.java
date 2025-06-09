package com.api.validatejwt.v1.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.extern.slf4j.Slf4j;

/**
 * DTO para representar o resultado da validação de um token JWT.
 * Contém flag indicando se o JWT é válido e uma mensagem descritiva.
 * Implementa {@link Serializable} para permitir transporte ou cache.
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Indica se o token JWT foi validado com sucesso.
     */
    private Boolean isValid;

    /**
     * Mensagem explicativa sobre o resultado da validação.
     */
    private String message;

    /**
     * Construtor que inicializa somente o status da validação.
     * Define mensagem padrão de sucesso.
     *
     * @param isValid Resultado da validação do token JWT
     */
    public JwtDTO(Boolean isValid) {
        this.isValid = isValid;
        this.message = "Validado com sucesso";
        log.debug("JwtDTO criado com isValid: {}", isValid);
    }

    /**
     * Registra log de erro detalhado ao indicar resultado inválido.
     * Deve ser chamado quando a validação falha para rastreamento.
     *
     * @param errorMessage Mensagem detalhada do erro ocorrido na validação
     * @param ex Exceção associada ao erro (opcional, pode ser null)
     */
    public void logValidationError(String errorMessage, Exception ex) {
        if (ex != null) {
            log.error("Falha na validação do JWT: {}. Traceback registrado.", errorMessage, ex);
        } else {
            log.error("Falha na validação do JWT: {}", errorMessage);
        }
    }
}
