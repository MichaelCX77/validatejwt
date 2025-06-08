package com.api.validatejwt.v1.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * {@code RequestIdFilter} é um filtro servlet responsável por garantir que
 * cada requisição HTTP contenha um identificador único (Request-ID).
 * Esse ID é utilizado para correlação de logs, facilitando a observabilidade
 * e rastreamento em ambientes distribuídos.
 * <p>
 * O Request-ID é obtido do cabeçalho "X-Request-ID" ou gerado automaticamente
 * como um UUID, e injetado no MDC (Mapped Diagnostic Context) para log estruturado.
 */
@Component
@Slf4j
public class RequestIdFilter implements Filter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String METHOD_KEY = "method";
    private static final String PATH_KEY = "path";
    private static final String CLIENT_IP_KEY = "clientIp";
    private static final String REQUEST_ID_KEY = "requestId";

    /**
     * Adiciona o identificador da requisição no MDC e nos cabeçalhos da resposta HTTP.
     * Limpa automaticamente o MDC após o término da requisição.
     *
     * @param request  Requisição do cliente
     * @param response Resposta do servidor
     * @param chain    Cadeia de execução do filtro
     * @throws IOException      Caso ocorra erro de entrada/saída
     * @throws ServletException Caso ocorra erro de servlet
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Extrai ou gera um novo requestId
        String requestId = resolveRequestId(httpRequest);

        // Insere informações úteis no MDC
        putMdcValues(requestId, httpRequest);

        // Adiciona o requestId no cabeçalho da resposta para rastreamento externo
        httpResponse.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            chain.doFilter(request, response);
        } catch (Exception ex) {
            // Log estruturado com traceback em caso de falha inesperada durante o filtro
            log.error("Erro inesperado durante a execução do filtro de Request-ID: {}", ex.getMessage(), ex);
            throw ex;
        } finally {
            // Garante que os dados de contexto sejam removidos ao fim da requisição
            MDC.clear();
            log.debug("MDC limpo após o fim da requisição de ID {}", requestId);
        }
    }

    /**
     * Gera ou recupera o identificador único da requisição a partir do cabeçalho.
     *
     * @param request Requisição HTTP
     * @return ID da requisição
     */
    private String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);

        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
            log.debug("Nenhum Request-ID encontrado. Gerado novo ID: {}", requestId);
        } else {
            log.debug("Request-ID recebido no cabeçalho: {}", requestId);
        }

        return requestId;
    }

    /**
     * Preenche o MDC com informações úteis para rastreamento nos logs.
     *
     * @param requestId ID único da requisição
     * @param request   Objeto da requisição HTTP
     */
    private void putMdcValues(String requestId, HttpServletRequest request) {
        MDC.put(REQUEST_ID_KEY, requestId);
        MDC.put(METHOD_KEY, request.getMethod());
        MDC.put(PATH_KEY, request.getRequestURI());
        MDC.put(CLIENT_IP_KEY, getClientIp(request));
    }

    /**
     * Obtém o IP do cliente, respeitando proxies e balanceadores (X-Forwarded-For).
     *
     * @param request Requisição HTTP
     * @return IP do cliente
     */
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
    }
}
