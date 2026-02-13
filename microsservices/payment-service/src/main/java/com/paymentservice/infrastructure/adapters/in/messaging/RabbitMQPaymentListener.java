package com.paymentservice.infrastructure.adapters.in.messaging;

import com.paymentservice.application.ports.in.ProcessPaymentUseCase;
import com.paymentservice.infrastructure.adapters.in.messaging.dto.OrderMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// Driving adapter: this service is event-driven. Instead of REST calls triggering
// the use case, RabbitMQ messages do. This is the "worker" pattern.
@Component
public class RabbitMQPaymentListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQPaymentListener.class);
    private final ProcessPaymentUseCase processPaymentUseCase;

    public RabbitMQPaymentListener(ProcessPaymentUseCase processPaymentUseCase) {
        this.processPaymentUseCase = processPaymentUseCase;
    }

    @RabbitListener(queues = "${flash.rabbitmq.queue.order-created}")
    public void processPayment(OrderMessageDTO message) {
        log.info("Received order {} for payment processing (amount: {})", message.id(), message.totalAmount());
        processPaymentUseCase.execute(message.id(), message.totalAmount());
        log.info("Payment processing completed for order {}", message.id());
    }
}
