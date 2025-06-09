package com.api.validatejwt.v1.enums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste unitário do enum EnumRole, cobrindo a validação de papéis permitidos
 * e a listagem de valores válidos definidos no sistema.
 */
@DisplayName("Teste unitário do EnumRole")
class EnumRoleTest {

    private String validRoleAdmin;
    private String validRoleMember;
    private String validRoleExternal;

    private String invalidRole;
    private String emptyRole;
    private String nullRole;

    @BeforeEach
    void setup() {
        validRoleAdmin = "Admin";
        validRoleMember = "member"; // Testa case-insensitive
        validRoleExternal = "EXTERNAL";

        invalidRole = "Root";
        emptyRole = "";
        nullRole = null;
    }

    @Nested
    @DisplayName("Quando verificar papéis válidos")
    class RolesValidos {

        @Test
        @DisplayName("Deve aceitar role 'Admin'")
        void deveAceitarAdmin() {
            assertTrue(EnumRole.isValidRole(validRoleAdmin));
        }

        @Test
        @DisplayName("Deve aceitar role 'member' (case-insensitive)")
        void deveAceitarMemberCaseInsensitive() {
            assertTrue(EnumRole.isValidRole(validRoleMember));
        }

        @Test
        @DisplayName("Deve aceitar role 'EXTERNAL' (case-insensitive)")
        void deveAceitarExternalCaseInsensitive() {
            assertTrue(EnumRole.isValidRole(validRoleExternal));
        }
    }

    @Nested
    @DisplayName("Quando verificar papéis inválidos")
    class RolesInvalidos {

        @Test
        @DisplayName("Deve recusar role desconhecida")
        void deveRecusarRoleDesconhecida() {
            assertFalse(EnumRole.isValidRole(invalidRole));
        }

        @Test
        @DisplayName("Deve recusar role vazia")
        void deveRecusarRoleVazia() {
            assertFalse(EnumRole.isValidRole(emptyRole));
        }

        @Test
        @DisplayName("Deve recusar role nula")
        void deveRecusarRoleNula() {
            assertFalse(EnumRole.isValidRole(nullRole));
        }
    }

    @Test
    @DisplayName("Deve retornar todos os roles disponíveis como string")
    void deveRetornarRolesDisponiveis() {
        String resultado = EnumRole.availableRoles();

        assertTrue(resultado.contains("Admin"));
        assertTrue(resultado.contains("Member"));
        assertTrue(resultado.contains("External"));
    }
}
