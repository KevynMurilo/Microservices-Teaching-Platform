package com.orderservice.application.ports.out;

import com.orderservice.domain.model.Order;

// Outbound port: defines WHAT the domain needs from the messaging system.
// Whether we use RabbitMQ, Kafka, or SQS — the domain doesn't care.
// Swapping the broker only requires a new adapter implementing this interface.
public interface OrderEventPublisherPort {
    void publishOrderCreatedEvent(Order order);
}
