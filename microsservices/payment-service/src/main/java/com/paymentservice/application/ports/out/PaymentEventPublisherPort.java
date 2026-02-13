package com.paymentservice.application.ports.out;

import com.paymentservice.domain.model.Payment;

public interface PaymentEventPublisherPort {
    void publishPaymentProcessedEvent(Payment payment);
}
