package com.notificationservice.domain.service;

import com.notificationservice.application.ports.in.FindNotificationUseCase;
import com.notificationservice.application.ports.out.NotificationRepositoryPort;
import com.notificationservice.domain.model.Notification;

import java.util.List;

// Domain service: retrieves all notifications using the repository port.
// No Spring annotations here — wired via BeanConfiguration.
public class FindNotificationServiceImpl implements FindNotificationUseCase {

    private final NotificationRepositoryPort port;

    public FindNotificationServiceImpl(NotificationRepositoryPort port) {
        this.port = port;
    }

    @Override
    public List<Notification> execute() {
        return port.findAll();
    }
}
