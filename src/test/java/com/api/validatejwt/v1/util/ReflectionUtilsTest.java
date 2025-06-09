package com.api.validatejwt.v1.util;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Teste unitário do ReflectionUtils")
class ReflectionUtilsTest {

    private Class<?> clazzComJsonProperty;
    private Class<?> clazzSemJsonProperty;

    @BeforeEach
    void setup() {
        clazzComJsonProperty = ClasseComJsonProperty.class;
        clazzSemJsonProperty = ClasseSemJsonProperty.class;
    }

    @Nested
    @DisplayName("Quando a classe possui campos com @JsonProperty")
    class ComJsonProperty {

        @Test
        @DisplayName("Deve retornar nomes definidos na anotação")
        void deveRetornarNomesAnotados() {
            String[] fieldNames = ReflectionUtils.getFieldNames(clazzComJsonProperty);

            assertNotNull(fieldNames);
            assertEquals(2, fieldNames.length);
            assertEquals("id_customizado", fieldNames[0]);
            assertEquals("nome_customizado", fieldNames[1]);
        }
    }

    @Nested
    @DisplayName("Quando a classe não possui campos com @JsonProperty")
    class SemJsonProperty {

        @Test
        @DisplayName("Deve retornar os nomes originais dos campos")
        void deveRetornarNomesOriginais() {
            String[] fieldNames = ReflectionUtils.getFieldNames(clazzSemJsonProperty);

            assertNotNull(fieldNames);
            assertEquals(2, fieldNames.length);
            assertEquals("id", fieldNames[0]);
            assertEquals("nome", fieldNames[1]);
        }
    }

    // Classes auxiliares simulando cenários reais de uso
    private static class ClasseComJsonProperty {

        @JsonProperty("id_customizado")
        private Long id;

        @JsonProperty("nome_customizado")
        private String nome;
    }

    @Data
    private static class ClasseSemJsonProperty {
        private Long id;
        private String nome;
    }
}
