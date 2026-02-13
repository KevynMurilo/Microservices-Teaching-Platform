package com.paymentservice.infrastructure.adapters.out.messaging;

import com.paymentservice.application.ports.out.PaymentEventPublisherPort;
import com.paymentservice.domain.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQPaymentPublisher implements PaymentEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQPaymentPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    @Value("${flash.rabbitmq.exchange.payments}")
    private String exchange;

    @Value("${flash.rabbitmq.routing-key.payment-processed}")
    private String routingKey;

    public RabbitMQPaymentPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishPaymentProcessedEvent(Payment payment) {
        rabbitTemplate.convertAndSend(exchange, routingKey, payment);
        log.info("Published PaymentProcessedEvent for order {} with status {}",
                payment.getOrderId(), payment.getStatus());
    }
}
