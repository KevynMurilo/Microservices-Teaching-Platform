package com.notificationservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Declares RabbitMQ topology: exchanges, queues, bindings, and DLQs.
//
// Fan-out pattern explained:
// This service does NOT create its own exchanges. It binds its OWN queues to the EXISTING
// exchanges declared by order-service and payment-service. When a producer publishes a message
// to an exchange, RabbitMQ delivers a COPY to every queue bound with a matching routing key.
//
// This is why TopicExchange is used instead of DirectExchange or FanoutExchange:
//   - TopicExchange supports routing key patterns (e.g., "orders.v1.created")
//   - Multiple queues can bind to the same exchange with the same routing key
//   - Each consumer gets its own independent copy — no message competition
//
// Example flow for an order event:
//   1. order-service publishes to "orders.exchange" with key "orders.v1.created"
//   2. RabbitMQ delivers to "orders.v1.queue" (payment-service) AND "orders.v1.queue.notifications" (this service)
//   3. Each service processes the message independently
//
// Docs: https://www.rabbitmq.com/tutorials/tutorial-five-spring-amqp
@Configuration
public class RabbitMQConfig {

    // --- Orders Exchange (reuse the same exchange declared by order-service) ---

    @Value("${flash.rabbitmq.exchange.orders}")
    private String ordersExchangeName;

    @Value("${flash.rabbitmq.queue.order-notifications}")
    private String orderNotificationsQueueName;

    @Value("${flash.rabbitmq.routing-key.order-created}")
    private String ordersRoutingKey;

    @Bean
    public TopicExchange ordersExchange() {
        // This declares the same exchange as order-service. RabbitMQ is idempotent:
        // if the exchange already exists with the same config, this is a no-op.
        return new TopicExchange(ordersExchangeName);
    }

    // Main queue with Dead Letter Queue (DLQ) configuration.
    // When a message fails after all retries, RabbitMQ routes it to the DLX
    // instead of dropping it. This prevents data loss.
    // Docs: https://www.rabbitmq.com/docs/dlx
    @Bean
    public Queue orderNotificationsQueue() {
        return QueueBuilder.durable(orderNotificationsQueueName)
                .withArgument("x-dead-letter-exchange", ordersExchangeName + ".notifications.dlx")
                .withArgument("x-dead-letter-routing-key", ordersRoutingKey + ".notifications.dlq")
                .build();
    }

    @Bean
    public Binding orderNotificationsBinding(Queue orderNotificationsQueue, TopicExchange ordersExchange) {
        return BindingBuilder.bind(orderNotificationsQueue).to(ordersExchange).with(ordersRoutingKey);
    }

    // --- DLQ for order notifications ---

    @Bean
    public DirectExchange orderNotificationsDeadLetterExchange() {
        return new DirectExchange(ordersExchangeName + ".notifications.dlx");
    }

    @Bean
    public Queue orderNotificationsDeadLetterQueue() {
        return QueueBuilder.durable(orderNotificationsQueueName + ".dlq").build();
    }

    @Bean
    public Binding orderNotificationsDeadLetterBinding() {
        return BindingBuilder.bind(orderNotificationsDeadLetterQueue())
                .to(orderNotificationsDeadLetterExchange())
                .with(ordersRoutingKey + ".notifications.dlq");
    }

    // --- Payments Exchange (reuse the same exchange declared by payment-service) ---

    @Value("${flash.rabbitmq.exchange.payments}")
    private String paymentsExchangeName;

    @Value("${flash.rabbitmq.queue.payment-notifications}")
    private String paymentNotificationsQueueName;

    @Value("${flash.rabbitmq.routing-key.payment-processed}")
    private String paymentsRoutingKey;

    @Bean
    public TopicExchange paymentsExchange() {
        return new TopicExchange(paymentsExchangeName);
    }

    @Bean
    public Queue paymentNotificationsQueue() {
        return QueueBuilder.durable(paymentNotificationsQueueName)
                .withArgument("x-dead-letter-exchange", paymentsExchangeName + ".notifications.dlx")
                .withArgument("x-dead-letter-routing-key", paymentsRoutingKey + ".notifications.dlq")
                .build();
    }

    @Bean
    public Binding paymentNotificationsBinding() {
        return BindingBuilder.bind(paymentNotificationsQueue())
                .to(paymentsExchange())
                .with(paymentsRoutingKey);
    }

    // --- DLQ for payment notifications ---

    @Bean
    public DirectExchange paymentNotificationsDeadLetterExchange() {
        return new DirectExchange(paymentsExchangeName + ".notifications.dlx");
    }

    @Bean
    public Queue paymentNotificationsDeadLetterQueue() {
        return QueueBuilder.durable(paymentNotificationsQueueName + ".dlq").build();
    }

    @Bean
    public Binding paymentNotificationsDeadLetterBinding() {
        return BindingBuilder.bind(paymentNotificationsDeadLetterQueue())
                .to(paymentNotificationsDeadLetterExchange())
                .with(paymentsRoutingKey + ".notifications.dlq");
    }

    // Converts Java objects to JSON when sending/receiving messages.
    // Without this, RabbitMQ uses Java serialization (fragile and insecure).
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
