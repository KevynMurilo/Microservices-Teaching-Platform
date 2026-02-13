package com.orderservice.infrastructure.adapters.in.messaging;

import com.orderservice.application.ports.in.UpdateOrderStatusUseCase;
import com.orderservice.infrastructure.adapters.in.messaging.dto.PaymentMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// Driving adapter: receives payment result events from RabbitMQ.
// This closes the async loop: Order -> Payment -> back to Order with result.
@Component
public class RabbitMQPaymentListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQPaymentListener.class);
    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    public RabbitMQPaymentListener(UpdateOrderStatusUseCase updateOrderStatusUseCase) {
        this.updateOrderStatusUseCase = updateOrderStatusUseCase;
    }

    @RabbitListener(queues = "${flash.rabbitmq.queue.payment-processed}")
    public void handlePaymentProcessed(PaymentMessageDTO message) {
        log.info("Received payment result for order {}: {}", message.orderId(), message.status());
        updateOrderStatusUseCase.execute(message.orderId(), message.status());
        log.info("Order {} status updated to {}", message.orderId(), message.status());
    }
}
