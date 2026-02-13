package com.notificationservice.infrastructure.adapters.in.messaging;

import com.notificationservice.application.ports.in.CreateNotificationUseCase;
import com.notificationservice.domain.model.enums.NotificationType;
import com.notificationservice.infrastructure.adapters.in.messaging.dto.PaymentEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// Driving adapter: receives payment-processed events from RabbitMQ.
//
// Fan-out pattern in action:
// The payment-service publishes ONE message to "payments.exchange" with routing key "payments.v1.processed".
// Multiple services can each bind their OWN queue to the same exchange and routing key.
//
// In this architecture:
//   - order-service has "payments.v1.queue" bound to "payments.exchange"
//   - notification-service has "payments.v1.queue.notifications" bound to "payments.exchange"
// Both receive the same event independently. The order-service updates the order status,
// while the notification-service creates a notification — each with its own copy of the message.
//
// Docs: https://www.rabbitmq.com/tutorials/tutorial-five-spring-amqp
@Component
public class RabbitMQPaymentEventListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQPaymentEventListener.class);
    private final CreateNotificationUseCase createNotificationUseCase;

    public RabbitMQPaymentEventListener(CreateNotificationUseCase createNotificationUseCase) {
        this.createNotificationUseCase = createNotificationUseCase;
    }

    @RabbitListener(queues = "${flash.rabbitmq.queue.payment-notifications}")
    public void handlePaymentProcessed(PaymentEventDTO event) {
        log.info("Received payment-processed event for order {}: status={}", event.orderId(), event.status());

        // Determine notification type based on the payment status
        boolean isSuccess = "SUCCESS".equalsIgnoreCase(event.status());
        NotificationType type = isSuccess ? NotificationType.PAYMENT_SUCCESS : NotificationType.PAYMENT_FAILED;

        String message = isSuccess
                ? String.format("Payment approved for order %s", event.orderId())
                : String.format("Payment failed for order %s. Reason: %s", event.orderId(), event.failureReason());

        createNotificationUseCase.execute(event.orderId(), type, message);
        log.info("Notification created for order {} (type: {})", event.orderId(), type);
    }
}
