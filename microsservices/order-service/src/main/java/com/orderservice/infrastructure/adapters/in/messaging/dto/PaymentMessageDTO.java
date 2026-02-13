package com.orderservice.infrastructure.adapters.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentMessageDTO(UUID orderId, String status) {
}
