package com.notificationservice.infrastructure.adapters.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.UUID;

// DTO for deserializing order-created events from RabbitMQ.
// @JsonIgnoreProperties ensures forward compatibility: if the producer adds new fields,
// this consumer won't break. Always use this on event DTOs.
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderEventDTO(UUID id, String customerId, BigDecimal totalAmount) {
}
