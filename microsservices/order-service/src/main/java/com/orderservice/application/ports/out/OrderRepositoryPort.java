package com.orderservice.application.ports.out;

import com.orderservice.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Outbound port: defines WHAT the domain needs from persistence.
// The domain never knows about JPA, Hibernate, or any database technology.
// The actual implementation lives in infrastructure/adapters/out/persistence/.
public interface OrderRepositoryPort {
    Order save(Order order);
    List<Order> findAll();
    Optional<Order> findById(UUID id);
}
