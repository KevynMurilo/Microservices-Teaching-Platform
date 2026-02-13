package com.orderservice.infrastructure.adapters.out.messaging.dto;

import java.math.BigDecimal;
import java.util.UUID;

// DTO sent over RabbitMQ. Never send your domain model over the wire —
// it couples consumers to your internal structure.
// Docs: https://www.rabbitmq.com/tutorials/tutorial-one-spring-amqp
public record OrderCreatedEventDTO(UUID id, String customerId, BigDecimal totalAmount) {
}
