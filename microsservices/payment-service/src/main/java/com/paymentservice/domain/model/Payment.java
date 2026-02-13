package com.paymentservice.domain.model;

import com.paymentservice.domain.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// Rich Domain Model: state transitions and validation enforced here.
// No framework annotations — pure Java.
public class Payment {

    private UUID id;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String failureReason;
    private LocalDateTime processedAt;

    // Constructor for processing a new payment
    public Payment(UUID orderId, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.amount = amount;
        this.status = PaymentStatus.PROCESSING;
        validate();
    }

    // Constructor for reconstituting from the database
    public Payment(UUID id, UUID orderId, BigDecimal amount, PaymentStatus status,
                   String failureReason, LocalDateTime processedAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.failureReason = failureReason;
        this.processedAt = processedAt;
    }

    public void markAsSuccessful() {
        this.status = PaymentStatus.SUCCESS;
        this.processedAt = LocalDateTime.now();
        this.failureReason = null;
    }

    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.processedAt = LocalDateTime.now();
        this.failureReason = reason;
    }

    private void validate() {
        if (this.amount == null || this.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        if (this.orderId == null) {
            throw new IllegalArgumentException("Order ID is required.");
        }
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public String getFailureReason() { return failureReason; }
    public LocalDateTime getProcessedAt() { return processedAt; }
}
