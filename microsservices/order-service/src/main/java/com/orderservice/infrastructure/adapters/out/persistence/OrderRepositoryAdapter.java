package com.orderservice.infrastructure.adapters.out.persistence;

import com.orderservice.application.ports.out.OrderRepositoryPort;
import com.orderservice.domain.model.Order;
import com.orderservice.domain.model.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Driven adapter: implements the outbound port using Spring Data JPA.
// This class is the BRIDGE between the domain model and the JPA entity.
// The domain never touches OrderEntity — this adapter handles the mapping.
@Component
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository springDataRepository;

    public OrderRepositoryAdapter(SpringDataOrderRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setCustomerId(order.getCustomerId());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setStatus(order.getStatus().name());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());
        springDataRepository.save(entity);
        return order;
    }

    @Override
    public List<Order> findAll() {
        return springDataRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    private Order toDomain(OrderEntity entity) {
        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                entity.getTotalAmount(),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
