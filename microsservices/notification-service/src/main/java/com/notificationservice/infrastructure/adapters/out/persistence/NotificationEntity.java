package com.notificationservice.infrastructure.adapters.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

// JPA entity: maps to the database table. Lives ONLY in the infrastructure layer.
// The domain model (Notification.java) has NO JPA annotations — this separation is intentional.
// Avoid @Data from Lombok on JPA entities: it overrides hashCode()/equals() in ways
// that cause infinite loops with lazy-loaded relationships.
// Docs: https://docs.spring.io/spring-boot/reference/data/sql.html
@Entity
@Table(name = "tb_notifications")
public class NotificationEntity {

    @Id
    // No @GeneratedValue: the ID comes pre-filled from the domain model (UUID.randomUUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public NotificationEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
