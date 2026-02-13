package com.paymentservice.domain.service;

import com.paymentservice.application.ports.in.FindPaymentUseCase;
import com.paymentservice.application.ports.out.PaymentRepositoryPort;
import com.paymentservice.domain.model.Payment;

import java.util.List;

public class FindPaymentServiceImpl implements FindPaymentUseCase {

    private final PaymentRepositoryPort repositoryPort;

    public FindPaymentServiceImpl(PaymentRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public List<Payment> execute() {
        return repositoryPort.findAll();
    }
}
