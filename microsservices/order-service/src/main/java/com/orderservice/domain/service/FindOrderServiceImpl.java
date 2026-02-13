package com.orderservice.domain.service;

import com.orderservice.application.ports.in.FindOrderUseCase;
import com.orderservice.application.ports.out.OrderRepositoryPort;
import com.orderservice.domain.model.Order;

import java.util.List;

public class FindOrderServiceImpl implements FindOrderUseCase {

    private final OrderRepositoryPort port;

    public FindOrderServiceImpl(OrderRepositoryPort port) {
        this.port = port;
    }

    @Override
    public List<Order> execute() {
        return port.findAll();
    }
}
