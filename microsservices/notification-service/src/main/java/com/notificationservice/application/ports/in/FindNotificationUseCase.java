package com.notificationservice.application.ports.in;

import com.notificationservice.domain.model.Notification;

import java.util.List;

// Inbound port: defines HOW the outside world can query notifications.
// The REST controller (driving adapter) depends on this interface, not on the service impl.
public interface FindNotificationUseCase {
    List<Notification> execute();
}
