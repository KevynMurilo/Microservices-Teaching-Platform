package com.paymentservice.infrastructure.config;

import com.paymentservice.application.ports.in.FindPaymentUseCase;
import com.paymentservice.application.ports.in.ProcessPaymentUseCase;
import com.paymentservice.application.ports.out.PaymentEventPublisherPort;
import com.paymentservice.application.ports.out.PaymentRepositoryPort;
import com.paymentservice.domain.service.FindPaymentServiceImpl;
import com.paymentservice.domain.service.ProcessPaymentServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class BeanConfiguration {

    // The max payment amount is externalized to application.properties.
    // Domain classes can't use @Value (no Spring annotations allowed),
    // so we inject the value here and pass it via constructor.
    @Bean
    public ProcessPaymentUseCase processPaymentUseCase(
            PaymentRepositoryPort paymentRepositoryPort,
            PaymentEventPublisherPort paymentEventPublisherPort,
            @Value("${flash.payment.max-amount}") BigDecimal maxAmount) {
        return new ProcessPaymentServiceImpl(paymentRepositoryPort, paymentEventPublisherPort, maxAmount);
    }

    @Bean
    public FindPaymentUseCase findPaymentUseCase(PaymentRepositoryPort paymentRepositoryPort) {
        return new FindPaymentServiceImpl(paymentRepositoryPort);
    }
}
