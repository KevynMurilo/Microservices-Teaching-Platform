package com.orderservice.domain.service;

import com.orderservice.application.ports.in.UpdateOrderStatusUseCase;
import com.orderservice.application.ports.out.OrderRepositoryPort;
import com.orderservice.domain.model.Order;

import java.util.UUID;

public class UpdateOrderStatusServiceImpl implements UpdateOrderStatusUseCase {

    private final OrderRepositoryPort orderRepositoryPort;

    public UpdateOrderStatusServiceImpl(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public void execute(UUID orderId, String paymentStatus) {
        Order order = orderRepositoryPort.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Rich domain model: state transition rules enforced inside Order itself
        if ("SUCCESS".equals(paymentStatus)) {
            order.approve();
        } else if ("FAILED".equals(paymentStatus)) {
            order.reject();
        }

        orderRepositoryPort.save(order);
    }
}
