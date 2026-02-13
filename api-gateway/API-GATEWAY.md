# API Gateway

**Port:** 8080 | **Role:** Single entry point for all microservices.

## Responsibility

The API Gateway is the only service exposed to clients. It:
1. Routes requests to the correct downstream service based on URL path
2. Applies circuit breakers (Resilience4j) to prevent cascading failures
3. Returns fallback responses when downstream services are unavailable
4. Handles CORS for browser clients
5. Propagates distributed tracing context (via Micrometer + OTLP)

## Why a Gateway?

In a system with 20+ microservices, clients should never know about individual service URLs. The gateway:
- Provides a **single URL** for all APIs
- **Hides the internal network** — services can move, scale, or be replaced without client changes
- **Centralizes cross-cutting concerns** — auth, rate limiting, CORS, circuit breakers

## Routes

| Route | Downstream Service | Circuit Breaker |
|-------|-------------------|-----------------|
| `/api/orders/**` | order-service:8081 | orderServiceCB |
| `/api/payments/**` | payment-service:8082 | paymentServiceCB |
| `/api/notifications/**` | notification-service:8083 | notificationServiceCB |

## Circuit Breaker Configuration

```yaml
resilience4j:
  circuitbreaker:
    configs:
      default:
        sliding-window-size: 10           # Evaluates last 10 calls
        failure-rate-threshold: 50        # Opens circuit at 50% failure rate
        wait-duration-in-open-state: 10s  # Waits 10s before trying again
        permitted-number-of-calls-in-half-open-state: 3  # Tests 3 calls before deciding
  timelimiter:
    configs:
      default:
        timeout-duration: 3s              # Times out after 3 seconds
```

**States:** CLOSED (normal) → OPEN (failing, returns fallback) → HALF_OPEN (testing recovery)

When the circuit is open, the gateway returns:
```json
{
  "status": 503,
  "code": "SERVICE_UNAVAILABLE",
  "message": "The downstream service is currently unavailable. Please try again later."
}
```

## Spring Boot Version Note

The gateway uses Spring Boot **3.5.10** (not 4.0.2 like the other services) because Spring Cloud Gateway depends on the Spring Cloud release train, which may not yet support Boot 4.x. This version independence is intentional — the gateway is a separate deployable with its own lifecycle.

## Official Documentation

- [Spring Cloud Gateway](https://docs.spring.io/spring-cloud-gateway/reference/)
- [Resilience4j Circuit Breaker](https://resilience4j.readme.io/docs/circuitbreaker)
- [CORS Configuration](https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/cors-configuration.html)
