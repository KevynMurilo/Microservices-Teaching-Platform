package com.paymentservice.domain.service;

import com.paymentservice.application.ports.in.ProcessPaymentUseCase;
import com.paymentservice.application.ports.out.PaymentEventPublisherPort;
import com.paymentservice.application.ports.out.PaymentRepositoryPort;
import com.paymentservice.domain.model.Payment;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class ProcessPaymentServiceImpl implements ProcessPaymentUseCase {

    private final PaymentRepositoryPort paymentRepositoryPort;
    private final PaymentEventPublisherPort paymentEventPublisherPort;
    private final BigDecimal maxAmount;

    public ProcessPaymentServiceImpl(PaymentRepositoryPort paymentRepositoryPort,
                                     PaymentEventPublisherPort paymentEventPublisherPort,
                                     BigDecimal maxAmount) {
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.paymentEventPublisherPort = paymentEventPublisherPort;
        this.maxAmount = maxAmount;
    }

    @Override
    public void execute(UUID orderId, BigDecimal amount) {
        // Idempotency guard: if this order was already processed, skip.
        // In distributed systems, the same message can be delivered more than once
        // (at-least-once delivery). Without this check, customers get charged twice.
        // The UNIQUE constraint on order_id in the database is the safety net.
        Optional<Payment> existing = paymentRepositoryPort.findByOrderId(orderId);
        if (existing.isPresent()) {
            return;
        }

        Payment payment = new Payment(orderId, amount);

        if (amount.compareTo(maxAmount) > 0) {
            payment.markAsFailed("Amount exceeds safety limit of " + maxAmount);
        } else {
            payment.markAsSuccessful();
        }

        paymentRepositoryPort.save(payment);
        paymentEventPublisherPort.publishPaymentProcessedEvent(payment);
    }
}
