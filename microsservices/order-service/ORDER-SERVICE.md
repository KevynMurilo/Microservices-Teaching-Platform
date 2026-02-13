# Order Service

**Port:** 8081 | **Role:** Captures orders and publishes events asynchronously.

## Responsibility

The Order Service is the **producer** in the event-driven architecture. It:
1. Receives order creation requests via REST
2. Persists the order with `PENDING` status
3. Publishes an `OrderCreatedEvent` to RabbitMQ
4. Listens for `PaymentProcessedEvent` to update order status to `APPROVED` or `REJECTED`

## Hexagonal Architecture

```
domain/                              # Pure Java — zero framework annotations
├── model/
│   ├── Order.java                   # Rich domain model with state transitions
│   └── enums/OrderStatus.java       # PENDING, APPROVED, REJECTED, CANCELLED
└── service/
    ├── CreateOrderServiceImpl.java   # Orchestrates: create → save → publish
    ├── FindOrderServiceImpl.java
    └── UpdateOrderStatusServiceImpl.java

application/ports/                   # Interfaces (contracts)
├── in/                              # How the world calls us (driving)
│   ├── CreateOrderUseCase.java
│   ├── FindOrderUseCase.java
│   └── UpdateOrderStatusUseCase.java
└── out/                             # How we call the world (driven)
    ├── OrderRepositoryPort.java     # → Implemented by JPA adapter
    └── OrderEventPublisherPort.java # → Implemented by RabbitMQ adapter

infrastructure/                      # Framework-specific implementations
├── adapters/
│   ├── in/
│   │   ├── rest/OrderController.java           # REST driving adapter
│   │   └── messaging/RabbitMQPaymentListener.java  # Message driving adapter
│   └── out/
│       ├── persistence/OrderRepositoryAdapter.java # JPA driven adapter
│       └── messaging/RabbitMQOrderPublisher.java   # RabbitMQ driven adapter
└── config/
    ├── BeanConfiguration.java       # Wires domain classes (the "glue")
    ├── RabbitMQConfig.java          # Exchange, queue, DLQ declarations
    └── OpenApiConfig.java           # Swagger configuration
```

## API Endpoints

### POST /api/orders
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": "customer-123", "totalAmount": 99.90}'
```
**Response (201):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "customerId": "customer-123",
  "totalAmount": 99.90,
  "status": "PENDING",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T10:30:00"
}
```

### GET /api/orders
Returns all orders with their current status.

## RabbitMQ

| Direction | Exchange | Queue | Routing Key |
|-----------|----------|-------|-------------|
| **Publishes to** | orders.exchange (Topic) | — | orders.v1.created |
| **Consumes from** | payments.exchange | payments.v1.queue | payments.v1.processed |

**DLQ:** `orders.v1.queue.dlq` (failed messages after 3 retries)

## Database Schema

```sql
CREATE TABLE tb_orders (
    id            UUID PRIMARY KEY,
    customer_id   VARCHAR(255) NOT NULL,
    total_amount  NUMERIC(19, 2) NOT NULL,
    status        VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP NOT NULL
);
```

## Key Patterns

- **Rich Domain Model:** State transitions (`approve()`, `reject()`) live inside `Order.java`, not in services
- **Publisher Confirms:** `spring.rabbitmq.publisher-confirm-type=correlated` ensures messages are persisted by the broker
- **DTO over the wire:** `OrderCreatedEventDTO` is sent instead of the domain model, decoupling consumers from internal structure

## Official Documentation

- [Spring Boot](https://docs.spring.io/spring-boot/reference/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/)
- [RabbitMQ Spring AMQP](https://www.rabbitmq.com/tutorials/tutorial-one-spring-amqp)
- [Flyway](https://documentation.red-gate.com/fd)
- [SpringDoc OpenAPI](https://springdoc.org/)
