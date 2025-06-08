package com.api.validatejwt.v1.component;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestIdFilter implements Filter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestId = httpRequest.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put("requestId", requestId);

        httpResponse.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
        }
    }
}