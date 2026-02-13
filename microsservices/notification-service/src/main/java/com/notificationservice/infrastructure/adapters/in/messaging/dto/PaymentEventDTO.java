package com.notificationservice.infrastructure.adapters.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

// DTO for deserializing payment-processed events from RabbitMQ.
// @JsonIgnoreProperties ensures forward compatibility: if the producer adds new fields,
// this consumer won't break. Always use this on event DTOs.
@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentEventDTO(UUID orderId, String status, String failureReason) {
}
