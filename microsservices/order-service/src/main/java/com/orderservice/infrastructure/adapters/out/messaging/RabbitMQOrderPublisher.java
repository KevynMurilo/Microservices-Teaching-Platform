package com.orderservice.infrastructure.adapters.out.messaging;

import com.orderservice.application.ports.out.OrderEventPublisherPort;
import com.orderservice.domain.model.Order;
import com.orderservice.infrastructure.adapters.out.messaging.dto.OrderCreatedEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Driven adapter: publishes domain events to RabbitMQ.
// Uses a DTO to decouple the message schema from the domain model.
// Docs: https://www.rabbitmq.com/tutorials/tutorial-three-spring-amqp
@Component
public class RabbitMQOrderPublisher implements OrderEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQOrderPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${flash.rabbitmq.exchange.name}")
    private String exchange;

    @Value("${flash.rabbitmq.routing-key.order-created}")
    private String routingKey;

    public RabbitMQOrderPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishOrderCreatedEvent(Order order) {
        var event = new OrderCreatedEventDTO(order.getId(), order.getCustomerId(), order.getTotalAmount());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        log.info("Published OrderCreatedEvent for order {}", order.getId());
    }
}
