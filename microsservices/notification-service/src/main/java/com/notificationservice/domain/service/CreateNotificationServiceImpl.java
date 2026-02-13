package com.notificationservice.domain.service;

import com.notificationservice.application.ports.in.CreateNotificationUseCase;
import com.notificationservice.application.ports.out.NotificationRepositoryPort;
import com.notificationservice.domain.model.Notification;
import com.notificationservice.domain.model.enums.NotificationType;

import java.util.UUID;

// Domain service: orchestrates use case logic using ports (interfaces).
// No Spring annotations here — wired via BeanConfiguration.
public class CreateNotificationServiceImpl implements CreateNotificationUseCase {

    private final NotificationRepositoryPort notificationRepositoryPort;

    public CreateNotificationServiceImpl(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }

    @Override
    public Notification execute(UUID orderId, NotificationType type, String message) {
        Notification notification = new Notification(orderId, type, message);
        return notificationRepositoryPort.save(notification);
    }
}
