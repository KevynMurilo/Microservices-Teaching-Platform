package com.notificationservice.domain.model.enums;

// Represents the different events that can trigger a notification.
// Each type maps to a specific event consumed from RabbitMQ.
public enum NotificationType {
    ORDER_CREATED,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED
}
