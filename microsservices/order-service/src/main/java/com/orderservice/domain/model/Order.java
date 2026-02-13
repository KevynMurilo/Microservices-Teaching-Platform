package com.orderservice.domain.model;

import com.orderservice.domain.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// This is a Rich Domain Model: business rules live HERE, not in services.
// Notice: zero framework annotations. This class is pure Java.
// Docs: https://alistair.cockburn.us/hexagonal-architecture/
public class Order {

    private UUID id;
    private String customerId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor for creating a NEW order (business rule: always starts as PENDING)
    public Order(String customerId, BigDecimal totalAmount) {
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        validate();
    }

    // Constructor for reconstituting an order from the database (via adapter mapper)
    public Order(UUID id, String customerId, BigDecimal totalAmount, OrderStatus status,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // State transition: only PENDING orders can be approved
    public void approve() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be approved.");
        }
        this.status = OrderStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }

    // State transition: only PENDING orders can be rejected
    public void reject() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be rejected.");
        }
        this.status = OrderStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    private void validate() {
        if (this.totalAmount == null || this.totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order amount must be greater than zero.");
        }
        if (this.customerId == null || this.customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required.");
        }
    }

    public UUID getId() { return id; }
    public String getCustomerId() { return customerId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
