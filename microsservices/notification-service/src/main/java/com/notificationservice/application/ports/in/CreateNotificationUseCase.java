package com.notificationservice.application.ports.in;

import com.notificationservice.domain.model.Notification;
import com.notificationservice.domain.model.enums.NotificationType;

import java.util.UUID;

// Inbound port: defines HOW the outside world can create notifications.
// The messaging listeners (driving adapters) depend on this interface, not on the service impl.
// Docs: https://alistair.cockburn.us/hexagonal-architecture/
public interface CreateNotificationUseCase {
    Notification execute(UUID orderId, NotificationType type, String message);
}
