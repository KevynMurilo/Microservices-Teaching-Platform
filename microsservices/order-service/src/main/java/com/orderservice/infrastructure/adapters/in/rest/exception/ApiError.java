package com.orderservice.infrastructure.adapters.in.rest.exception;

import java.time.Instant;

// Standardized error response returned by all endpoints.
// Consistent error format makes life easier for frontend teams and API consumers.
public record ApiError(int status, String code, String message, Instant timestamp) {
    public ApiError(int status, String code, String message) {
        this(status, code, message, Instant.now());
    }
}
