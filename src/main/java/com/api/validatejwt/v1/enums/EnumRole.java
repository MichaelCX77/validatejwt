package com.api.validatejwt.v1.enums;

import java.util.Arrays;

import lombok.extern.slf4j.Slf4j;

/**
 * Enum que representa os papéis (roles) aceitos no sistema.
 * Pode ser usado para controle de acesso e validação de permissões.
 */
@Slf4j
public enum EnumRole {
    Admin, Member, External;

    /**
     * Verifica se um papel informado é válido, ou seja,
     * se corresponde a um dos papéis definidos neste enum.
     *
     * @param role Papel a ser validado
     * @return true se o papel for válido, false caso contrário
     */
    public static boolean isValidRole(String role) {
        if (role == null) {
            log.debug("Tentativa de validar role nula.");
            return false;
        }

        boolean isValid = Arrays.stream(EnumRole.values())
                .anyMatch(enumRole -> enumRole.name().equalsIgnoreCase(role));

        if (!isValid) {
            log.warn("Role inválido informado: {}. Roles permitidos: {}", role, availableRoles());
        } else {
            log.debug("Role válido identificado: {}", role);
        }

        return isValid;
    }

    /**
     * Retorna uma String com todos os papéis disponíveis.
     *
     * @return String contendo os valores válidos do enum
     */
    public static String availableRoles() {
        return Arrays.toString(EnumRole.values());
    }
}
