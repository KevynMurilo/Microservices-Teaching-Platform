package com.paymentservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Payment service declares the payments exchange, queue, and DLQ.
// The orders.v1.queue is declared by order-service; this service only consumes from it.
@Configuration
public class RabbitMQConfig {

    @Value("${flash.rabbitmq.exchange.payments}")
    private String exchangeName;

    @Value("${flash.rabbitmq.queue.payment-processed}")
    private String queueName;

    @Value("${flash.rabbitmq.routing-key.payment-processed}")
    private String routingKey;

    @Bean
    public TopicExchange paymentsExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Queue paymentProcessedQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", exchangeName + ".dlx")
                .withArgument("x-dead-letter-routing-key", routingKey + ".dlq")
                .build();
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentProcessedQueue())
                .to(paymentsExchange())
                .with(routingKey);
    }

    // --- Dead Letter Queue ---

    @Bean
    public DirectExchange paymentDeadLetterExchange() {
        return new DirectExchange(exchangeName + ".dlx");
    }

    @Bean
    public Queue paymentDeadLetterQueue() {
        return QueueBuilder.durable(queueName + ".dlq").build();
    }

    @Bean
    public Binding paymentDeadLetterBinding() {
        return BindingBuilder.bind(paymentDeadLetterQueue())
                .to(paymentDeadLetterExchange())
                .with(routingKey + ".dlq");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
