package com.paymentservice.infrastructure.adapters.in.rest.exception;

import java.time.Instant;

public record ApiError(int status, String code, String message, Instant timestamp) {
    public ApiError(int status, String code, String message) {
        this(status, code, message, Instant.now());
    }
}
