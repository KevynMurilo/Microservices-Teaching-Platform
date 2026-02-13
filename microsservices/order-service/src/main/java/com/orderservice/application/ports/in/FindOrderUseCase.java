package com.orderservice.application.ports.in;

import com.orderservice.domain.model.Order;

import java.util.List;

public interface FindOrderUseCase {
    List<Order> execute();
}
