package com.orderservice.domain.service;

import com.orderservice.application.ports.in.CreateOrderUseCase;
import com.orderservice.application.ports.out.OrderEventPublisherPort;
import com.orderservice.application.ports.out.OrderRepositoryPort;
import com.orderservice.domain.model.Order;

import java.math.BigDecimal;

// Domain service: orchestrates use case logic using ports (interfaces).
// No Spring annotations here — wired via BeanConfiguration.
public class CreateOrderServiceImpl implements CreateOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final OrderEventPublisherPort orderEventPublisherPort;

    public CreateOrderServiceImpl(OrderRepositoryPort orderRepositoryPort,
                                  OrderEventPublisherPort orderEventPublisherPort) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.orderEventPublisherPort = orderEventPublisherPort;
    }

    @Override
    public Order execute(String customerId, BigDecimal totalAmount) {
        Order order = new Order(customerId, totalAmount);
        Order savedOrder = orderRepositoryPort.save(order);
        orderEventPublisherPort.publishOrderCreatedEvent(savedOrder);
        return savedOrder;
    }
}
