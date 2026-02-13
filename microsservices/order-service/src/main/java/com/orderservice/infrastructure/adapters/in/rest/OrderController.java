package com.orderservice.infrastructure.adapters.in.rest;

import com.orderservice.application.ports.in.CreateOrderUseCase;
import com.orderservice.application.ports.in.FindOrderUseCase;
import com.orderservice.domain.model.Order;
import com.orderservice.infrastructure.adapters.in.rest.dto.OrderRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Driving adapter: translates HTTP requests into use case calls.
// The controller depends on the PORT (interface), not the service implementation.
// Spring resolves the concrete bean via BeanConfiguration.
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final FindOrderUseCase findOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, FindOrderUseCase findOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.findOrderUseCase = findOrderUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(findOrderUseCase.execute());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        Order order = createOrderUseCase.execute(request.customerId(), request.totalAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
