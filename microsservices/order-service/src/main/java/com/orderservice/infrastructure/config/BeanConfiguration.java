package com.orderservice.infrastructure.config;

import com.orderservice.application.ports.in.CreateOrderUseCase;
import com.orderservice.application.ports.in.FindOrderUseCase;
import com.orderservice.application.ports.in.UpdateOrderStatusUseCase;
import com.orderservice.application.ports.out.OrderEventPublisherPort;
import com.orderservice.application.ports.out.OrderRepositoryPort;
import com.orderservice.domain.service.CreateOrderServiceImpl;
import com.orderservice.domain.service.FindOrderServiceImpl;
import com.orderservice.domain.service.UpdateOrderStatusServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// This is the "glue" of hexagonal architecture.
// Domain classes have NO Spring annotations, so Spring can't auto-discover them.
// This @Configuration manually wires domain services, injecting adapter implementations
// through the port interfaces. This is how we keep the domain framework-free.
@Configuration
public class BeanConfiguration {

    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderRepositoryPort orderRepositoryPort,
                                                  OrderEventPublisherPort orderEventPublisherPort) {
        return new CreateOrderServiceImpl(orderRepositoryPort, orderEventPublisherPort);
    }

    @Bean
    public FindOrderUseCase findOrderUseCase(OrderRepositoryPort port) {
        return new FindOrderServiceImpl(port);
    }

    @Bean
    public UpdateOrderStatusUseCase updateOrderStatusUseCase(OrderRepositoryPort port) {
        return new UpdateOrderStatusServiceImpl(port);
    }
}
