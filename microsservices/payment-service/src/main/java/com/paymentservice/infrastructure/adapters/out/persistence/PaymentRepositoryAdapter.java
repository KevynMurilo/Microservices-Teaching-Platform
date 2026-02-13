package com.paymentservice.infrastructure.adapters.out.persistence;

import com.paymentservice.application.ports.out.PaymentRepositoryPort;
import com.paymentservice.domain.model.Payment;
import com.paymentservice.domain.model.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final SpringDataPaymentRepository repository;

    public PaymentRepositoryAdapter(SpringDataPaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = new PaymentEntity();
        entity.setId(payment.getId());
        entity.setOrderId(payment.getOrderId());
        entity.setAmount(payment.getAmount());
        entity.setStatus(payment.getStatus().name());
        entity.setFailureReason(payment.getFailureReason());
        entity.setProcessedAt(payment.getProcessedAt());
        repository.save(entity);
        return payment;
    }

    @Override
    public List<Payment> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId).map(this::toDomain);
    }

    private Payment toDomain(PaymentEntity entity) {
        return new Payment(
                entity.getId(),
                entity.getOrderId(),
                entity.getAmount(),
                PaymentStatus.valueOf(entity.getStatus()),
                entity.getFailureReason(),
                entity.getProcessedAt()
        );
    }
}
