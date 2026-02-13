package com.orderservice.infrastructure.adapters.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

// Java Records are ideal for DTOs: immutable, compact, auto-generated equals/hashCode.
// @Valid on the controller parameter triggers these validation annotations.
// Docs: https://docs.spring.io/spring-boot/reference/io/validation.html
public record OrderRequestDTO(
        @NotBlank(message = "Customer ID is required")
        String customerId,

        @NotNull(message = "Total amount is required")
        @Positive(message = "Total amount must be greater than zero")
        BigDecimal totalAmount
) {}
