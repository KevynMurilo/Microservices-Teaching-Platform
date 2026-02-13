# Microservices Teaching Platform

A production-grade microservices system designed as a **hands-on learning platform** for development teams. Every pattern implemented here is used in real big-tech environments.

## Architecture Overview

```
                           ┌─────────────────┐
                           │   API Gateway    │ :8080
                           │ (Circuit Breaker)│
                           └────────┬────────┘
                    ┌───────────────┼───────────────┐
                    │               │               │
              ┌─────▼─────┐  ┌─────▼─────┐  ┌──────▼──────┐
              │  Order     │  │  Payment  │  │Notification │
              │  Service   │  │  Service  │  │  Service    │
              │  :8081     │  │  :8082    │  │  :8083      │
              └──┬──┬──────┘  └──┬──┬─────┘  └──┬──┬──────┘
                 │  │            │  │            │  │
              ┌──▼┐ │         ┌──▼┐ │         ┌──▼┐ │
              │PG │ │         │PG │ │         │PG │ │
              └───┘ │         └───┘ │         └───┘ │
                    │               │               │
              ┌─────▼───────────────▼───────────────▼────┐
              │              RabbitMQ                      │
              │    orders.exchange ◄── payments.exchange   │
              │     (Topic)              (Topic)          │
              └───────────────────────────────────────────┘

              ┌─────────────────────────────────────────┐
              │           OBSERVABILITY                  │
              │  Jaeger :16686  │  Prometheus :9090      │
              │  Grafana :3000  │                        │
              └─────────────────────────────────────────┘
```

## Patterns Demonstrated

| Pattern | Where | Docs |
|---------|-------|------|
| **Hexagonal Architecture** (Ports & Adapters) | All services | [Reference](https://alistair.cockburn.us/hexagonal-architecture/) |
| **Database-per-Service** | Each service has its own PostgreSQL | [Reference](https://microservices.io/patterns/data/database-per-service.html) |
| **Event-Driven Architecture** | RabbitMQ async messaging | [Reference](https://www.rabbitmq.com/tutorials) |
| **API Gateway** | Spring Cloud Gateway | [Reference](https://docs.spring.io/spring-cloud-gateway/reference/) |
| **Circuit Breaker** | Resilience4j in Gateway | [Reference](https://resilience4j.readme.io/docs/circuitbreaker) |
| **Dead Letter Queue (DLQ)** | Failed messages routed to `.dlq` queues | [Reference](https://www.rabbitmq.com/docs/dlx) |
| **Idempotency** | Payment deduplication by order ID | [Reference](https://microservices.io/patterns/communication-style/idempotent-consumer.html) |
| **Fan-Out** | One event consumed by multiple services | [Reference](https://www.rabbitmq.com/tutorials/tutorial-five-spring-amqp) |
| **Distributed Tracing** | Micrometer + OpenTelemetry + Jaeger | [Reference](https://micrometer.io/docs/tracing) |
| **Metrics & Dashboards** | Prometheus + Grafana | [Reference](https://prometheus.io/docs/) |
| **Schema Migrations** | Flyway versioned migrations | [Reference](https://documentation.red-gate.com/fd) |
| **Containerization** | Docker multi-stage builds | [Reference](https://docs.docker.com/build/building/multi-stage/) |

## Quick Start

```bash
docker-compose up --build
```

Wait for all containers to be healthy (~60 seconds), then:

```bash
# Create an order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": "customer-123", "totalAmount": 99.90}'

# Check orders (status should change from PENDING to APPROVED)
curl http://localhost:8080/api/orders

# Check payments
curl http://localhost:8080/api/payments

# Check notifications (fan-out: receives both order and payment events)
curl http://localhost:8080/api/notifications
```

## Service Endpoints

| Service | Port | Endpoints |
|---------|------|-----------|
| API Gateway | 8080 | Routes all `/api/*` requests |
| Order Service | 8081 | `POST /api/orders`, `GET /api/orders` |
| Payment Service | 8082 | `GET /api/payments` |
| Notification Service | 8083 | `GET /api/notifications` |

## Observability URLs

| Tool | URL | Credentials |
|------|-----|-------------|
| RabbitMQ Management | http://localhost:15672 | guest / guest |
| Jaeger (Tracing) | http://localhost:16686 | — |
| Prometheus (Metrics) | http://localhost:9090 | — |
| Grafana (Dashboards) | http://localhost:3000 | admin / admin |
| Swagger (Order) | http://localhost:8081/swagger-ui.html | — |
| Swagger (Payment) | http://localhost:8082/swagger-ui.html | — |
| Swagger (Notification) | http://localhost:8083/swagger-ui.html | — |

## Event Flow

```
1. Client POST /api/orders → API Gateway → Order Service
2. Order Service saves order (PENDING) → publishes OrderCreatedEvent to RabbitMQ
3. RabbitMQ fans out the event to:
   ├── Payment Service (orders.v1.queue) → processes payment → publishes PaymentProcessedEvent
   └── Notification Service (orders.v1.queue.notifications) → creates ORDER_CREATED notification
4. PaymentProcessedEvent fans out to:
   ├── Order Service (payments.v1.queue) → updates order to APPROVED/REJECTED
   └── Notification Service (payments.v1.queue.notifications) → creates PAYMENT_SUCCESS/FAILED notification
```

## Chaos Testing Guide

### 1. Service Resilience (Buffering)

```bash
# Stop the payment service
docker stop flash-payment-service

# Send orders — they should succeed (HTTP 201) because order-service is independent
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": "test", "totalAmount": 50.00}'

# Check RabbitMQ UI: messages are buffered in orders.v1.queue
# → http://localhost:15672

# Restart payment service — it processes all buffered messages
docker start flash-payment-service
```

### 2. Dead Letter Queue (DLQ)

Force a processing error and verify the message lands in the `.dlq` queue after 3 retries. Check in the RabbitMQ Management UI.

### 3. Idempotency

Send the same order message twice to the queue. Verify only one payment is created (check `GET /api/payments`).

### 4. Circuit Breaker

```bash
# Stop order-service
docker stop flash-order-service

# Call through gateway — should get 503 fallback (not a timeout)
curl http://localhost:8080/api/orders
# Response: {"status":503,"code":"SERVICE_UNAVAILABLE","message":"..."}
```

## Distributed Tracing

After creating an order, open **Jaeger UI** at http://localhost:16686:

1. Select `order-service` from the Service dropdown
2. Click **Find Traces**
3. Open a trace to see spans across: Order Service → RabbitMQ → Payment Service → RabbitMQ → Order Service

This works because `spring.rabbitmq.listener.simple.observation-enabled=true` propagates trace context through messages.

## Project Structure

```
microsservices/
├── docker-compose.yml              # All 11 containers
├── README.md                       # This file
├── infra/
│   ├── prometheus/prometheus.yml    # Scrape config
│   └── grafana/                    # Dashboards + provisioning
├── api-gateway/                    # Spring Cloud Gateway + Circuit Breaker
│   └── API-GATEWAY.md
├── order-service/                  # Order capture + event publishing
│   └── ORDER-SERVICE.md
├── payment-service/                # Async payment processing + idempotency
│   └── PAYMENT-SERVICE.md
└── notification-service/           # Fan-out consumer for all events
    └── NOTIFICATION-SERVICE.md
```

## Per-Service Documentation

- [API Gateway](api-gateway/API-GATEWAY.md)
- [Order Service](microsservices/order-service/ORDER-SERVICE.md)
- [Payment Service](microsservices/payment-service/PAYMENT-SERVICE.md)
- [Notification Service](microsservices/notification-service/NOTIFICATION-SERVICE.md)

## Tech Stack

| Technology | Purpose | Docs |
|-----------|---------|------|
| Java 17 | Language | — |
| Spring Boot 4.0.2 | Framework (services) | [Docs](https://docs.spring.io/spring-boot/reference/) |
| Spring Boot 3.5.10 | Framework (gateway) | [Docs](https://docs.spring.io/spring-boot/reference/) |
| Spring Cloud Gateway | API routing | [Docs](https://docs.spring.io/spring-cloud-gateway/reference/) |
| RabbitMQ 3.13 | Message broker | [Docs](https://www.rabbitmq.com/tutorials) |
| PostgreSQL 16 | Database | [Docs](https://www.postgresql.org/docs/16/) |
| Flyway | Schema migrations | [Docs](https://documentation.red-gate.com/fd) |
| Resilience4j | Circuit breaker | [Docs](https://resilience4j.readme.io/docs) |
| Micrometer + OTel | Distributed tracing | [Docs](https://micrometer.io/docs/tracing) |
| Prometheus | Metrics collection | [Docs](https://prometheus.io/docs/) |
| Grafana | Dashboards | [Docs](https://grafana.com/docs/grafana/latest/) |
| Jaeger | Trace visualization | [Docs](https://www.jaegertracing.io/docs/) |
| Docker | Containerization | [Docs](https://docs.docker.com/) |
