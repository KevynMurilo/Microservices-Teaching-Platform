package com.orderservice.application.ports.in;

import com.orderservice.domain.model.Order;

import java.math.BigDecimal;

// Inbound port: defines HOW the outside world can interact with our domain.
// The controller (driving adapter) depends on this interface, not on the service impl.
// Docs: https://alistair.cockburn.us/hexagonal-architecture/
public interface CreateOrderUseCase {
    Order execute(String customerId, BigDecimal totalAmount);
}
