package com.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

// Fallback controller: when the circuit breaker trips (downstream service is down),
// the gateway returns this response instead of timing out or throwing a 500.
// This gives the client a clear, actionable error message.
@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public ResponseEntity<Map<String, Object>> fallbackGet() {
        return fallbackResponse();
    }

    @PostMapping("/fallback")
    public ResponseEntity<Map<String, Object>> fallbackPost() {
        return fallbackResponse();
    }

    private ResponseEntity<Map<String, Object>> fallbackResponse() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", 503,
                        "code", "SERVICE_UNAVAILABLE",
                        "message", "The downstream service is currently unavailable. Please try again later.",
                        "timestamp", Instant.now().toString()
                ));
    }
}
