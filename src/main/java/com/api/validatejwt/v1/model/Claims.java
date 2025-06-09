package com.api.validatejwt.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import lombok.extern.slf4j.Slf4j;

/**
 * Representa as claims (declarações) de um token JWT que são esperadas e validadas na aplicação.
 * Inclui informações essenciais como papel do usuário, semente de autenticação e nome.
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Claims {

    /**
     * Papel ou perfil do usuário (exemplo: "ADMIN", "USER").
     * Campo obrigatório para validação da autorização.
     */
    @JsonProperty("Role")
    @NotBlank(message = "Role não pode estar em branco")
    private String role;

    /**
     * Semente (seed) usada para alguma forma de verificação ou controle do token.
     * Campo obrigatório para garantir integridade.
     */
    @JsonProperty("Seed")
    @NotBlank(message = "Seed não pode estar em branco")
    private String seed;

    /**
     * Nome do usuário ou entidade que está autenticada.
     * Campo obrigatório para identificação clara.
     */
    @JsonProperty("Name")
    @NotBlank(message = "Name não pode estar em branco")
    private String name;

    /**
     * Loga informações importantes no momento da criação do objeto Claims.
     * Pode ser chamado manualmente após a construção para auditoria/debug.
     */
    public void logInfo() {
        // Usado para debug e rastreamento da criação do objeto, sem expor dados sensíveis em produção
        log.debug("Claims criado com Role='{}', Seed='{}', Name='{}'", role, seed, name);
    }
}
