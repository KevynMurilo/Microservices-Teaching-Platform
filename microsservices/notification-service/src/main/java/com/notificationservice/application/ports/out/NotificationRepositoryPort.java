package com.notificationservice.application.ports.out;

import com.notificationservice.domain.model.Notification;

import java.util.List;

// Outbound port: defines WHAT the domain needs from persistence.
// The domain never knows about JPA, Hibernate, or any database technology.
// The actual implementation lives in infrastructure/adapters/out/persistence/.
public interface NotificationRepositoryPort {
    Notification save(Notification notification);
    List<Notification> findAll();
}
