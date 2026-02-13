package com.notificationservice.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// Spring Data JPA auto-generates the SQL implementation at runtime.
// Docs: https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
@Repository
public interface SpringDataNotificationRepository extends JpaRepository<NotificationEntity, UUID> {
}
