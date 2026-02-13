package com.orderservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Declares RabbitMQ topology: exchanges, queues, bindings, and DLQ.
// Using TopicExchange enables fan-out: multiple services can each have
// their own queue bound to the same exchange with different routing keys.
// Docs: https://www.rabbitmq.com/tutorials/tutorial-five-spring-amqp
@Configuration
public class RabbitMQConfig {

    @Value("${flash.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${flash.rabbitmq.queue.order-created}")
    private String queueName;

    @Value("${flash.rabbitmq.routing-key.order-created}")
    private String routingKey;

    // --- Orders Exchange (Topic for fan-out support) ---

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(exchangeName);
    }

    // Main queue with Dead Letter Queue (DLQ) configuration.
    // When a message fails after all retries, RabbitMQ routes it to the DLX
    // instead of dropping it. This prevents data loss.
    // Docs: https://www.rabbitmq.com/docs/dlx
    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", exchangeName + ".dlx")
                .withArgument("x-dead-letter-routing-key", routingKey + ".dlq")
                .build();
    }

    @Bean
    public Binding orderBinding(Queue orderCreatedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with(routingKey);
    }

    // --- Dead Letter Queue (DLQ) ---

    @Bean
    public DirectExchange orderDeadLetterExchange() {
        return new DirectExchange(exchangeName + ".dlx");
    }

    @Bean
    public Queue orderDeadLetterQueue() {
        return QueueBuilder.durable(queueName + ".dlq").build();
    }

    @Bean
    public Binding orderDeadLetterBinding() {
        return BindingBuilder.bind(orderDeadLetterQueue())
                .to(orderDeadLetterExchange())
                .with(routingKey + ".dlq");
    }

    // --- Payments return queue (consumed by this service) ---

    @Value("${flash.rabbitmq.exchange.payments}")
    private String paymentsExchangeName;

    @Value("${flash.rabbitmq.queue.payment-processed}")
    private String paymentsQueueName;

    @Value("${flash.rabbitmq.routing-key.payment-processed}")
    private String paymentsRoutingKey;

    @Bean
    public TopicExchange paymentsExchange() {
        return new TopicExchange(paymentsExchangeName);
    }

    @Bean
    public Queue paymentProcessedQueue() {
        return QueueBuilder.durable(paymentsQueueName).build();
    }

    @Bean
    public Binding paymentProcessedBinding() {
        return BindingBuilder.bind(paymentProcessedQueue())
                .to(paymentsExchange())
                .with(paymentsRoutingKey);
    }

    // Converts Java objects to JSON when sending/receiving messages.
    // Without this, RabbitMQ uses Java serialization (fragile and insecure).
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
