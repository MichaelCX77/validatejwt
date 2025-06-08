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
 * Filtro responsável por garantir que cada requisição HTTP tenha um identificador único (Request-ID).
 * Este ID é utilizado para rastreamento nos logs, facilitando a correlação de eventos e debugging.
 * 
 * O filtro recupera o Request-ID do cabeçalho HTTP "X-Request-ID" caso exista, ou gera um novo UUID.
 * Em seguida, o Request-ID é armazenado no MDC (Mapped Diagnostic Context) para inclusão automática nos logs
 * e também adicionado ao cabeçalho da resposta para que o cliente tenha conhecimento do ID gerado.
 */
@Component
@Slf4j
public class RequestIdFilter implements Filter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    /**
     * Método principal do filtro que adiciona ou recupera o Request-ID da requisição,
     * injeta no MDC para logs estruturados e garante sua limpeza após a conclusão da requisição.
     * Também garante que o Request-ID seja repassado na resposta HTTP.
     *
     * @param request  objeto que representa a requisição
     * @param response objeto que representa a resposta
     * @param chain    cadeia de filtros para continuar o processamento
     * @throws IOException      em caso de erro de I/O
     * @throws ServletException em caso de erro no servlet
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Tenta recuperar o Request-ID do cabeçalho da requisição
        String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);

        // Caso o Request-ID não exista ou seja inválido, gera um novo UUID
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
            log.debug("Nenhum Request-ID encontrado na requisição. Gerado novo ID: {}", requestId);
        } else {
            log.debug("Request-ID recebido na requisição: {}", requestId);
        }

        // Insere o Request-ID no MDC para que seja incluído automaticamente nos logs subsequentes
        MDC.put("requestId", requestId);

        // Define o Request-ID no cabeçalho da resposta para rastreamento externo
        httpResponse.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            // Continua o processamento da cadeia de filtros / servlet
            chain.doFilter(request, response);
        } finally {
            // Remove o Request-ID do MDC para evitar vazamento entre requisições diferentes
            MDC.remove("requestId");
            log.debug("Request-ID {} removido do MDC após processamento da requisição.", requestId);
        }
    }
}
