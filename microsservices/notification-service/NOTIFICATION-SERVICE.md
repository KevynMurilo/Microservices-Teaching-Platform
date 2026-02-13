# Notification Service

**Port:** 8083 | **Role:** Fan-out consumer that reacts to both order and payment events.

## Responsibility

The Notification Service demonstrates the **fan-out pattern**. It:
1. Listens for `OrderCreatedEvent` from RabbitMQ (separate queue from payment-service)
2. Listens for `PaymentProcessedEvent` from RabbitMQ (separate queue from order-service)
3. Creates notification records for each event
4. Exposes a REST endpoint to query notification history

## Why This Service Exists (Teaching Purpose)

This service demonstrates that **one event can trigger multiple consumers**. When `order-service` publishes an `OrderCreatedEvent`:
- `payment-service` consumes it from `orders.v1.queue` (to process payment)
- `notification-service` consumes it from `orders.v1.queue.notifications` (to log notification)

Both queues are bound to the same `orders.exchange` (TopicExchange). This is the **fan-out pattern**: one message, multiple independent consumers. Each consumer has its own queue, its own DLQ, and processes independently.

## Fan-Out Architecture

```
orders.exchange (Topic)
├── orders.v1.queue              → Payment Service (processes payment)
└── orders.v1.queue.notifications → Notification Service (logs notification)

payments.exchange (Topic)
├── payments.v1.queue              → Order Service (updates order status)
└── payments.v1.queue.notifications → Notification Service (logs notification)
```

## RabbitMQ

| Direction | Exchange | Queue | Routing Key |
|-----------|----------|-------|-------------|
| **Consumes from** | orders.exchange | orders.v1.queue.notifications | orders.v1.created |
| **Consumes from** | payments.exchange | payments.v1.queue.notifications | payments.v1.processed |

## Database Schema

```sql
CREATE TABLE tb_notifications (
    id         UUID PRIMARY KEY,
    order_id   UUID NOT NULL,
    type       VARCHAR(50) NOT NULL,     -- ORDER_CREATED, PAYMENT_SUCCESS, PAYMENT_FAILED
    message    VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

## Official Documentation

- [RabbitMQ Topic Exchange](https://www.rabbitmq.com/tutorials/tutorial-five-spring-amqp)
- [Fan-Out Pattern](https://www.enterpriseintegrationpatterns.com/patterns/messaging/PublishSubscribeChannel.html)
