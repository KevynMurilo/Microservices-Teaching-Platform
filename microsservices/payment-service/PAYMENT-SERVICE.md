# Payment Service

**Port:** 8082 | **Role:** Async worker that processes payments from order events.

## Responsibility

The Payment Service is a **consumer/worker**. It:
1. Listens for `OrderCreatedEvent` from RabbitMQ
2. Processes the payment (approve or reject based on amount)
3. Persists the payment result
4. Publishes `PaymentProcessedEvent` back to RabbitMQ
5. Exposes a REST endpoint to query payment history

## Hexagonal Architecture

```
domain/
├── model/
│   ├── Payment.java                  # Rich domain model
│   └── enums/PaymentStatus.java      # PROCESSING, SUCCESS, FAILED
└── service/
    ├── ProcessPaymentServiceImpl.java # Idempotency check + business rules
    └── FindPaymentServiceImpl.java

application/ports/
├── in/
│   ├── ProcessPaymentUseCase.java
│   └── FindPaymentUseCase.java
└── out/
    ├── PaymentRepositoryPort.java     # Includes findByOrderId for idempotency
    └── PaymentEventPublisherPort.java

infrastructure/
├── adapters/
│   ├── in/
│   │   ├── messaging/RabbitMQPaymentListener.java  # Event-driven entry point
│   │   └── rest/PaymentController.java
│   └── out/
│       ├── persistence/PaymentRepositoryAdapter.java
│       └── messaging/RabbitMQPaymentPublisher.java
└── config/
    ├── BeanConfiguration.java    # Injects configurable max-amount
    ├── RabbitMQConfig.java       # Payments exchange + DLQ
    └── OpenApiConfig.java
```

## Key Patterns

### Idempotency
The same message can be delivered multiple times (at-least-once delivery). Without idempotency, customers get charged twice.

**Application level:** `ProcessPaymentServiceImpl` checks `findByOrderId()` before processing.
**Database level:** `UNIQUE` constraint on `order_id` column as a safety net.

### Dead Letter Queue
Failed messages go to `payments.v1.queue.dlq` after 3 retries with exponential backoff (2s → 4s → 8s).

### Configurable Business Rules
The max payment amount is externalized to `application.properties` (`flash.payment.max-amount=10000`) and injected through `BeanConfiguration` — domain classes never touch `@Value`.

## RabbitMQ

| Direction | Exchange | Queue | Routing Key |
|-----------|----------|-------|-------------|
| **Consumes from** | orders.exchange | orders.v1.queue | orders.v1.created |
| **Publishes to** | payments.exchange (Topic) | — | payments.v1.processed |

## Database Schema

```sql
CREATE TABLE tb_payments (
    id             UUID PRIMARY KEY,
    order_id       UUID NOT NULL UNIQUE,  -- idempotency protection
    amount         NUMERIC(19, 2) NOT NULL,
    status         VARCHAR(50) NOT NULL,
    failure_reason VARCHAR(500),
    processed_at   TIMESTAMP
);
```

## Official Documentation

- [RabbitMQ Dead Letter Exchanges](https://www.rabbitmq.com/docs/dlx)
- [Idempotent Consumer Pattern](https://microservices.io/patterns/communication-style/idempotent-consumer.html)
- [Spring AMQP Retry](https://docs.spring.io/spring-amqp/reference/)
