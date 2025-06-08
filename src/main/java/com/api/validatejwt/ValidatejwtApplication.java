package com.api.validatejwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

/**
 * Classe principal da aplicação Spring Boot para o serviço ValidateJWT.
 * Responsável por inicializar o contexto da aplicação.
 */
@SpringBootApplication
@Slf4j
public class ValidatejwtApplication {

    /**
     * Ponto de entrada da aplicação. Inicia o Spring Boot com os argumentos fornecidos.
     *
     * @param args Argumentos de linha de comando
     */
    public static void main(String[] args) {
        try {
            log.debug("Inicializando a aplicação ValidateJWT.");
            SpringApplication.run(ValidatejwtApplication.class, args);
            log.debug("Aplicação iniciada com sucesso.");
        } catch (Exception ex) {
            // Registro estruturado de falha crítica com traceback
            log.error("Falha crítica ao iniciar a aplicação: {}", ex.getMessage(), ex);
            throw ex; // repropagando para manter o comportamento padrão
        }
    }
}
