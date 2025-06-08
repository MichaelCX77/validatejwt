package com.api.validatejwt.v1.util;

import org.slf4j.MDC;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;
    private long timestamp;
    private String requestId;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.requestId = MDC.get("requestId");
    }
}
