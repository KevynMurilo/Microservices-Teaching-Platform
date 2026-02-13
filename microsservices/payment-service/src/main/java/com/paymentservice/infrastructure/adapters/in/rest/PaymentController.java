package com.paymentservice.infrastructure.adapters.in.rest;

import com.paymentservice.application.ports.in.FindPaymentUseCase;
import com.paymentservice.domain.model.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final FindPaymentUseCase findPaymentUseCase;

    public PaymentController(FindPaymentUseCase findPaymentUseCase) {
        this.findPaymentUseCase = findPaymentUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(findPaymentUseCase.execute());
    }
}
