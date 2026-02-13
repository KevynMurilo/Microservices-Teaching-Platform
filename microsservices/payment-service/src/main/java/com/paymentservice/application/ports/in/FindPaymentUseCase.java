package com.paymentservice.application.ports.in;

import com.paymentservice.domain.model.Payment;

import java.util.List;

public interface FindPaymentUseCase {
    List<Payment> execute();
}
