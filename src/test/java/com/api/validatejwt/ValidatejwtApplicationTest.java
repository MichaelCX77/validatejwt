package com.api.validatejwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Teste unitário da classe de inicialização ValidatejwtApplication.
 * Garante a execução segura do método main com tratamento de exceções.
 */
@DisplayName("Teste unitário do ValidatejwtApplication")
class ValidatejwtApplicationTest {

    private String[] mockArgs;

    @BeforeEach
    void setup() {
        mockArgs = new String[]{"--spring.profiles.active=test"};
    }

    @Nested
    @DisplayName("Quando executar o método main com sucesso")
    class Sucesso {

        @Test
        @DisplayName("Deve iniciar a aplicação sem lançar exceções")
        void deveIniciarAplicacaoComSucesso() {
            try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
                mockedSpringApp.when(() -> SpringApplication.run(ValidatejwtApplication.class, mockArgs))
                               .thenReturn(null); // Simula retorno normal

                assertDoesNotThrow(() -> ValidatejwtApplication.main(mockArgs));

                mockedSpringApp.verify(() -> SpringApplication.run(ValidatejwtApplication.class, mockArgs), times(1));
            }
        }
    }

    @Nested
    @DisplayName("Quando ocorrer falha na inicialização")
    class Falha {

        @Test
        @DisplayName("Deve lançar exceção ao falhar iniciar a aplicação")
        void deveLancarExcecaoAoFalharInicializacao() {
            RuntimeException exceptionEsperada = new RuntimeException("Falha simulada");

            try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
                mockedSpringApp.when(() -> SpringApplication.run(ValidatejwtApplication.class, mockArgs))
                               .thenThrow(exceptionEsperada);

                RuntimeException exLancada = assertThrows(RuntimeException.class, () ->
                        ValidatejwtApplication.main(mockArgs));

                assertEquals("Falha simulada", exLancada.getMessage());

                mockedSpringApp.verify(() -> SpringApplication.run(ValidatejwtApplication.class, mockArgs), times(1));
            }
        }
    }
}
