package com.paymentservice.application.ports.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface ProcessPaymentUseCase {
    void execute(UUID orderId, BigDecimal amount);
}
