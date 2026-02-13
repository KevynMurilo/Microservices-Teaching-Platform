package com.paymentservice.infrastructure.adapters.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderMessageDTO(UUID id, BigDecimal totalAmount) {
}
