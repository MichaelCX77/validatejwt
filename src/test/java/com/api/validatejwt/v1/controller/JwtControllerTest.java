package com.api.validatejwt.v1.controller;

import com.api.validatejwt.v1.model.Jwt;
import com.api.validatejwt.v1.model.JwtDTO;
import com.api.validatejwt.v1.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Teste unitário do JwtController")
class JwtControllerTest {

    private JwtService jwtService;
    private JwtController jwtController;

    private Jwt mockJwt;
    private JwtDTO mockJwtDTO;

    @BeforeEach
    void setup() {
        jwtService = mock(JwtService.class);
        jwtController = new JwtController(jwtService);

        mockJwt = new Jwt();
        mockJwt.setJwt("eyJhbGciOi..."); // campo correto é 'jwt'

        mockJwtDTO = new JwtDTO(true, "Token válido");
    }

    @Nested
    @DisplayName("Quando validar um JWT com sucesso")
    class Sucesso {

        @Test
        @DisplayName("Deve retornar JwtDTO com status 200")
        void deveRetornarJwtDTOComStatus200() {
            when(jwtService.validate(mockJwt)).thenReturn(mockJwtDTO);

            ResponseEntity<JwtDTO> response = jwtController.validateJwt(mockJwt);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockJwtDTO, response.getBody());
            assertTrue(response.getBody().getIsValid());
            assertEquals("Token válido", response.getBody().getMessage());

            verify(jwtService, times(1)).validate(mockJwt);
        }
    }
}
