package com.notificationservice.infrastructure.adapters.out.persistence;

import com.notificationservice.application.ports.out.NotificationRepositoryPort;
import com.notificationservice.domain.model.Notification;
import com.notificationservice.domain.model.enums.NotificationType;
import org.springframework.stereotype.Component;

import java.util.List;

// Driven adapter: implements the outbound port using Spring Data JPA.
// This class is the BRIDGE between the domain model and the JPA entity.
// The domain never touches NotificationEntity — this adapter handles the mapping.
@Component
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final SpringDataNotificationRepository springDataRepository;

    public NotificationRepositoryAdapter(SpringDataNotificationRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(notification.getId());
        entity.setOrderId(notification.getOrderId());
        entity.setType(notification.getType().name());
        entity.setMessage(notification.getMessage());
        entity.setCreatedAt(notification.getCreatedAt());
        springDataRepository.save(entity);
        return notification;
    }

    @Override
    public List<Notification> findAll() {
        return springDataRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private Notification toDomain(NotificationEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getOrderId(),
                NotificationType.valueOf(entity.getType()),
                entity.getMessage(),
                entity.getCreatedAt()
        );
    }
}
