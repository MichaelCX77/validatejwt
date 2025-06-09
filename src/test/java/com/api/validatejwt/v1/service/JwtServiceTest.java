package com.api.validatejwt.v1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.api.validatejwt.v1.enums.EnumRole;
import com.api.validatejwt.v1.exception.ClientException;
import com.api.validatejwt.v1.model.Claim;
import com.api.validatejwt.v1.model.Jwt;
import com.api.validatejwt.v1.model.JwtDTO;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;

@DisplayName("Teste unitário do JwtService")
class JwtServiceTest {

    private Validator validator;
    private JwtService jwtService;

    private Jwt validJwt;

    @BeforeEach
    void setup() {
        validator = mock(Validator.class);
        jwtService = new JwtService(validator);

        String payloadJson = "{\"Name\":\"validName\",\"Seed\":\"2\",\"Role\":\"ADMIN\"}";
        String base64Payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String token = "header." + base64Payload + ".signature";

        validJwt = new Jwt();
        validJwt.setJwt(token);
    }

    @Nested
    @DisplayName("Validação com token JWT válido")
    class ValidaJwtValido {

        @Test
        @DisplayName("Deve validar JWT corretamente e retornar JwtDTO válido")
        void deveValidarJwtComSucesso() {
            when(validator.validate(any(Claim.class))).thenReturn(Collections.emptySet());

            JwtDTO resultado = jwtService.validate(validJwt);

            assertNotNull(resultado);
            assertTrue(resultado.getIsValid());
            assertEquals("Validado com sucesso", resultado.getMessage());
        }
    }

    @Nested
    @DisplayName("Testes de extração e parsing do JWT")
    class ExtracaoParsing {

        @Test
        @DisplayName("Deve lançar ClientException se token mal formado (partes != 3)")
        void deveLancarExceptionTokenMalFormado() {
            Jwt jwt = new Jwt();
            jwt.setJwt("token.invalido");

            ClientException exc = assertThrows(ClientException.class, () -> jwtService.validate(jwt));
            assertEquals("JWT inválido", exc.getMessage());
        }

        @Test
        @DisplayName("Deve lançar ClientException se payload Base64 inválido")
        void deveLancarExceptionPayloadBase64Invalido() {
            Jwt jwt = new Jwt();
            jwt.setJwt("header.payloadInvalido.signature");

            ClientException exc = assertThrows(ClientException.class, () -> jwtService.validate(jwt));
            assertEquals("JWT inválido", exc.getMessage());
        }

        @Test
        @DisplayName("Deve lançar ClientException para campo JSON inválido (UnrecognizedPropertyException)")
        void deveLancarExceptionCampoJsonInvalido() {
            String payloadJson = "{\"Name\":\"abc\",\"Seed\":\"2\",\"Role\":\"ADMIN\",\"campoInvalido\":\"x\"}";
            String base64Payload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            String token = "header." + base64Payload + ".signature";

            Jwt jwt = new Jwt();
            jwt.setJwt(token);

            ClientException exc = assertThrows(ClientException.class, () -> jwtService.validate(jwt));
            assertTrue(exc.getMessage().contains("Campo inválido no JSON"));
            assertTrue(exc.getMessage().contains("campoInvalido"));
        }
    }

    @Nested
    @DisplayName("Validação dos beans e campos dos claims")
    class ValidacoesClaims {

        @Test
        @DisplayName("Deve lançar ClientException se bean Claim inválido")
        void deveLancarExceptionBeanInvalido() {
        	
        	@SuppressWarnings("unchecked")
            ConstraintViolation<Claim> violation = mock(ConstraintViolation.class);
            Path path = mock(Path.class);
            when(path.toString()).thenReturn("Name");
            when(path.iterator()).thenReturn(Collections.emptyIterator());

            when(violation.getPropertyPath()).thenReturn(path);
            when(violation.getMessage()).thenReturn("não pode ser vazio");

            Set<ConstraintViolation<Claim>> violations = Collections.singleton(violation);

            when(validator.validate(any(Claim.class))).thenReturn(violations);

            ClientException exc = assertThrows(ClientException.class, () -> jwtService.validate(validJwt));
            assertTrue(exc.getMessage().contains("Campo 'Name' informado nos claims é inválido"));
        }

        @Test
        @DisplayName("Deve lançar ClientException se nome contiver números")
        void deveLancarExceptionNomeContemNumeros() {
            when(validator.validate(any())).thenReturn(Collections.emptySet());

            String payloadJson = "{\"Name\":\"Nome123\",\"Seed\":\"2\",\"Role\":\"ADMIN\"}";
            String base64Payload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            Jwt jwt = new Jwt();
            jwt.setJwt("header." + base64Payload + ".signature");

            ClientException exc = assertThrows(ClientException.class, () -> jwtService.validate(jwt));
            assertEquals("Claim inválido: 'Name' não pode conter números", exc.getMessage());
        }

        @Test
        @DisplayName("Deve lançar ClientException se nome exceder 256 caracteres")
        void deveLancarExceptionNomeExcedeTamanho() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 257; i++) sb.append("a");

            when(validator.validate(any())).thenReturn(Collections.emptySet());

            String payloadJson = String.format("{\"Name\":\"%s\",\"Seed\":\"2\",\"Role\":\"ADMIN\"}", sb);
            String base64Payload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            Jwt jwt = new Jwt();
            jwt.setJwt("header." + base64Payload + ".signature");

            ClientException exc = assertThrows(ClientException.class, () -> jwtService.validate(jwt));
            assertEquals("Claim inválido: 'Name' não pode exceder 256 caracteres", exc.getMessage());
        }

        @Test
        @DisplayName("Deve lançar ClientException para role inválida")
        void deveLancarExceptionRoleInvalida() {
            when(validator.validate(any())).thenReturn(Collections.emptySet());

            String payloadJson = "{\"Name\":\"nomeValido\",\"Seed\":\"2\",\"Role\":\"INVALID_ROLE\"}";
            String base64Payload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            Jwt jwt = new Jwt();
            jwt.setJwt("header." + base64Payload + ".signature");

            ClientException exc = assertThrows(ClientException.class, () -> jwtService.validate(jwt));
            assertTrue(exc.getMessage().contains("Role inválida"));
            assertTrue(exc.getMessage().contains("INVALID_ROLE"));
        }

        @Test
        @DisplayName("Deve aceitar role válida")
        void deveAceitarRoleValida() {
            when(validator.validate(any())).thenReturn(Collections.emptySet());

            for (EnumRole role : EnumRole.values()) {
                String payloadJson = String.format("{\"Name\":\"nomeValido\",\"Seed\":\"2\",\"Role\":\"%s\"}", role.name());
                String base64Payload = Base64.getUrlEncoder().withoutPadding()
                        .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
                Jwt jwt = new Jwt();
                jwt.setJwt("header." + base64Payload + ".signature");

                assertDoesNotThrow(() -> jwtService.validate(jwt));
            }
        }

        @Test
        @DisplayName("Deve lançar ClientException para seed não primo")
        void deveLancarExceptionSeedNaoPrimo() {
            when(validator.validate(any())).thenReturn(Collections.emptySet());

            String payloadJson = "{\"Name\":\"nomeValido\",\"Seed\":\"4\",\"Role\":\"ADMIN\"}";
            String base64Payload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            Jwt jwt = new Jwt();
            jwt.setJwt("header." + base64Payload + ".signature");

            ClientException exc = assertThrows(ClientException.class, () -> jwtService.validate(jwt));
            assertEquals("Claim inválido: 'Seed' deve ser um número primo", exc.getMessage());
        }

        @Test
        @DisplayName("Deve lançar ClientException para seed inválida (não número primo)")
        void deveLancarExceptionSeedInvalida() {
            when(validator.validate(any())).thenReturn(Collections.emptySet());

            // Dado um JWT com seed = 10 (não primo)
            String jwtInvalido = criarJwtComPayload("{\"Name\":\"Joao\",\"Seed\":\"10\",\"Role\":\"ADMIN\"}");
            Jwt jwt = new Jwt();
            jwt.setJwt(jwtInvalido);

            ClientException exc = assertThrows(ClientException.class, () -> jwtService.validate(jwt));
            assertEquals("Claim inválido: 'Seed' deve ser um número primo", exc.getMessage());
        }

        private String criarJwtComPayload(String payloadJson) {
            String header = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
            String payload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
            String signature = "signature"; // Ignorada
            return String.format("%s.%s.%s", header, payload, signature);
        }

    }
}
