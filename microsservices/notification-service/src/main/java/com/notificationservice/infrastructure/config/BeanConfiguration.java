package com.notificationservice.infrastructure.config;

import com.notificationservice.application.ports.in.CreateNotificationUseCase;
import com.notificationservice.application.ports.in.FindNotificationUseCase;
import com.notificationservice.application.ports.out.NotificationRepositoryPort;
import com.notificationservice.domain.service.CreateNotificationServiceImpl;
import com.notificationservice.domain.service.FindNotificationServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// This is the "glue" of hexagonal architecture.
// Domain classes have NO Spring annotations, so Spring can't auto-discover them.
// This @Configuration manually wires domain services, injecting adapter implementations
// through the port interfaces. This is how we keep the domain framework-free.
@Configuration
public class BeanConfiguration {

    @Bean
    public CreateNotificationUseCase createNotificationUseCase(NotificationRepositoryPort notificationRepositoryPort) {
        return new CreateNotificationServiceImpl(notificationRepositoryPort);
    }

    @Bean
    public FindNotificationUseCase findNotificationUseCase(NotificationRepositoryPort port) {
        return new FindNotificationServiceImpl(port);
    }
}
