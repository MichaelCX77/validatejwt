package com.api.validatejwt.v1.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * Intercepta todas as respostas dos controllers REST e aplica um padrão de envelope.
 * Acrescenta informações úteis como timestamp e requestId para rastreabilidade.
 * 
 * Exemplo de resposta:
 * {
 *   "data": { ... },
 *   "timestamp": 1680000000000,
 *   "requestId": "abc-123-def-456"
 * }
 */
@Slf4j
@RestControllerAdvice
public class ResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    /**
     * Define se a resposta deve ser interceptada.
     * Neste caso, todas as respostas serão processadas.
     *
     * @param returnType Tipo de retorno do método controller
     * @param converterType Tipo de conversor de mensagem
     * @return true para processar todas as respostas
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * Executado antes do corpo da resposta ser enviado ao cliente.
     * Aplica um wrapper com timestamp e requestId, mantendo o corpo original no campo "data".
     *
     * @param body Corpo da resposta original
     * @param returnType Tipo do método que gerou a resposta
     * @param selectedContentType Tipo de mídia da resposta
     * @param selectedConverterType Conversor utilizado
     * @param request Requisição original do cliente
     * @param response Resposta a ser enviada
     * @return Objeto envelopado com metadados
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        try {
            Map<String, Object> wrapped = new HashMap<>();
            wrapped.put("data", body);
            wrapped.put("timestamp", System.currentTimeMillis());
            wrapped.put("requestId", MDC.get("requestId")); // Rastreabilidade entre logs

            return wrapped;

        } catch (Exception ex) {
            // Fallback em caso de falha inesperada durante o wrapping
            log.error("Falha ao envelopar a resposta da API: {}", ex.getMessage(), ex);

            // Mesmo em caso de falha, retorna o corpo original sem o wrapper
            return body;
        }
    }
}
