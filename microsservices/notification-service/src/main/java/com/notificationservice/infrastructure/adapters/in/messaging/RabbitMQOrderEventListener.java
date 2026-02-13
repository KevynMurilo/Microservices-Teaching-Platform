package com.notificationservice.infrastructure.adapters.in.messaging;

import com.notificationservice.application.ports.in.CreateNotificationUseCase;
import com.notificationservice.domain.model.enums.NotificationType;
import com.notificationservice.infrastructure.adapters.in.messaging.dto.OrderEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// Driving adapter: receives order-created events from RabbitMQ.
//
// Fan-out pattern in action:
// The order-service publishes ONE message to "orders.exchange" with routing key "orders.v1.created".
// Multiple services can each bind their OWN queue to the same exchange and routing key.
// Each service gets its own COPY of the message — they don't compete for it.
//
// In this architecture:
//   - payment-service has "orders.v1.queue" bound to "orders.exchange"
//   - notification-service has "orders.v1.queue.notifications" bound to "orders.exchange"
// Both receive the same event independently. This is the power of TopicExchange.
//
// Docs: https://www.rabbitmq.com/tutorials/tutorial-five-spring-amqp
@Component
public class RabbitMQOrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQOrderEventListener.class);
    private final CreateNotificationUseCase createNotificationUseCase;

    public RabbitMQOrderEventListener(CreateNotificationUseCase createNotificationUseCase) {
        this.createNotificationUseCase = createNotificationUseCase;
    }

    @RabbitListener(queues = "${flash.rabbitmq.queue.order-notifications}")
    public void handleOrderCreated(OrderEventDTO event) {
        log.info("Received order-created event for order {}", event.id());

        String message = String.format(
                "Order %s created for customer %s with total amount %s",
                event.id(), event.customerId(), event.totalAmount()
        );

        createNotificationUseCase.execute(event.id(), NotificationType.ORDER_CREATED, message);
        log.info("Notification created for order {} (type: ORDER_CREATED)", event.id());
    }
}
