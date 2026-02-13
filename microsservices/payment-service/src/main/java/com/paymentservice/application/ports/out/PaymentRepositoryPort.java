package com.paymentservice.application.ports.out;

import com.paymentservice.domain.model.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);
    List<Payment> findAll();
    Optional<Payment> findByOrderId(UUID orderId);
}
