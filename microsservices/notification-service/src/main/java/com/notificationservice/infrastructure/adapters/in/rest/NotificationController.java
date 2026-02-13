package com.notificationservice.infrastructure.adapters.in.rest;

import com.notificationservice.application.ports.in.FindNotificationUseCase;
import com.notificationservice.domain.model.Notification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Driving adapter: translates HTTP requests into use case calls.
// The controller depends on the PORT (interface), not the service implementation.
// Spring resolves the concrete bean via BeanConfiguration.
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final FindNotificationUseCase findNotificationUseCase;

    public NotificationController(FindNotificationUseCase findNotificationUseCase) {
        this.findNotificationUseCase = findNotificationUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(findNotificationUseCase.execute());
    }
}
