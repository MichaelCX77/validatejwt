package com.api.validatejwt.v1.controller;

import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.validatejwt.v1.model.Jwt;
import com.api.validatejwt.v1.model.JwtDTO;
import com.api.validatejwt.v1.service.JwtService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável pela validação de tokens JWT recebidos via requisições HTTP.
 * Expõe endpoint REST para verificação de autenticidade e extração de informações do token.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class JwtController {

    private final JwtService jwtService;

    /**
     * Endpoint para validação de um token JWT.
     * 
     * @param jwt Objeto contendo o token a ser validado.
     * @return {@link ResponseEntity} contendo os dados extraídos do token.
     */
    @PostMapping("/jwt")
    public ResponseEntity<JwtDTO> validateJwt(@RequestBody @Valid Jwt jwt) {
        log.debug("Recebida requisição para validação de JWT");

        try {
            JwtDTO jwtDTO = jwtService.validate(jwt);
            log.debug("Validação de JWT concluída com sucesso");
            return ResponseEntity.ok(jwtDTO);
        } catch (Exception ex) {
            // Inclui status e log detalhado com traceback
            MDC.put("status", "500");
            log.error("Erro ao validar JWT: {}", ex.getMessage(), ex);
            throw ex; // Deixa o GlobalExceptionHandler lidar com a resposta padronizada
        }
    }
}
