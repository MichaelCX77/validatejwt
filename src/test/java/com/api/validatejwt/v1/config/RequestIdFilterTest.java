package com.api.validatejwt.v1.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@DisplayName("Teste unitário do RequestIdFilter")
class RequestIdFilterTest {

    private RequestIdFilter filter;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

    @BeforeEach
    void setup() {
        filter = new RequestIdFilter();
        httpRequest = mock(HttpServletRequest.class);
        httpResponse = mock(HttpServletResponse.class);
    }

    @Nested
    @DisplayName("doFilter")
    class DoFilterTests {

        @Test
        @DisplayName("Deve usar o Request-ID do cabeçalho se presente e manter MDC durante execução")
        void deveUsarRequestIdDoCabecalho() throws IOException, ServletException {
            String requestId = "test-request-id";

            when(httpRequest.getHeader("X-Request-ID")).thenReturn(requestId);
            when(httpRequest.getMethod()).thenReturn("GET");
            when(httpRequest.getRequestURI()).thenReturn("/api/test");
            when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);
            when(httpRequest.getRemoteAddr()).thenReturn("192.168.0.1");

            FilterChain spyChain = (req, res) -> {
                // Verifica o MDC durante a execução do filtro
                assertEquals(requestId, MDC.get("requestId"));
                assertEquals("GET", MDC.get("method"));
                assertEquals("/api/test", MDC.get("path"));
                assertEquals("192.168.0.1", MDC.get("clientIp"));
            };

            filter.doFilter(httpRequest, httpResponse, spyChain);

            verify(httpResponse).setHeader("X-Request-ID", requestId);
            // Após doFilter, MDC já deve estar limpo
            assertNull(MDC.get("requestId"));
        }

        @Test
        @DisplayName("Deve gerar novo Request-ID se cabeçalho não presente e manter MDC durante execução")
        void deveGerarNovoRequestIdSeAusente() throws IOException, ServletException {
            when(httpRequest.getHeader("X-Request-ID")).thenReturn(null);
            when(httpRequest.getMethod()).thenReturn("POST");
            when(httpRequest.getRequestURI()).thenReturn("/api/novo");
            when(httpRequest.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1, 10.0.0.2");
            when(httpRequest.getRemoteAddr()).thenReturn("192.168.0.2");

            final String[] capturedRequestId = new String[1];

            FilterChain spyChain = (req, res) -> {
                String requestIdInMdc = MDC.get("requestId");
                capturedRequestId[0] = requestIdInMdc;

                assertNotNull(requestIdInMdc);
                // Verifica se é UUID válido
                assertDoesNotThrow(() -> UUID.fromString(requestIdInMdc));
                assertEquals("POST", MDC.get("method"));
                assertEquals("/api/novo", MDC.get("path"));
                assertEquals("10.0.0.1", MDC.get("clientIp"));
            };

            filter.doFilter(httpRequest, httpResponse, spyChain);

            verify(httpResponse).setHeader(eq("X-Request-ID"), eq(capturedRequestId[0]));
            // Após doFilter, MDC já deve estar limpo
            assertNull(MDC.get("requestId"));
        }

        @Test
        @DisplayName("Deve limpar o MDC mesmo quando ocorre exceção durante o filtro")
        void deveLimparMdcMesmoComExcecao() {
            when(httpRequest.getHeader("X-Request-ID")).thenReturn("ex-request-id");
            when(httpRequest.getMethod()).thenReturn("DELETE");
            when(httpRequest.getRequestURI()).thenReturn("/api/error");
            when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);
            when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");

            FilterChain throwingChain = (req, res) -> {
                // Durante execução MDC está preenchido
                assertEquals("ex-request-id", MDC.get("requestId"));
                throw new RuntimeException("Erro simulado");
            };

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                filter.doFilter(httpRequest, httpResponse, throwingChain);
            });

            assertEquals("Erro simulado", thrown.getMessage());
            // Após exceção e finally, MDC deve estar limpo
            assertNull(MDC.get("requestId"));
        }
    }
}
