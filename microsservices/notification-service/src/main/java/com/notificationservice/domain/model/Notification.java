package com.notificationservice.domain.model;

import com.notificationservice.domain.model.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

// This is a Domain Model: business state and invariants live HERE, not in services.
// Notice: zero framework annotations. This class is pure Java.
// Docs: https://alistair.cockburn.us/hexagonal-architecture/
public class Notification {

    private UUID id;
    private UUID orderId;
    private NotificationType type;
    private String message;
    private LocalDateTime createdAt;

    // Constructor for creating a NEW notification (auto-generates ID and timestamp)
    public Notification(UUID orderId, NotificationType type, String message) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.type = type;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        validate();
    }

    // Constructor for reconstituting a notification from the database (via adapter mapper)
    public Notification(UUID id, UUID orderId, NotificationType type, String message, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.type = type;
        this.message = message;
        this.createdAt = createdAt;
    }

    private void validate() {
        if (this.orderId == null) {
            throw new IllegalArgumentException("Order ID is required.");
        }
        if (this.type == null) {
            throw new IllegalArgumentException("Notification type is required.");
        }
        if (this.message == null || this.message.trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message is required.");
        }
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public NotificationType getType() { return type; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
