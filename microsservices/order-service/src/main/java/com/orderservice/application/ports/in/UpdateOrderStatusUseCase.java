package com.orderservice.application.ports.in;

import java.util.UUID;

public interface UpdateOrderStatusUseCase {
    void execute(UUID orderId, String paymentStatus);
}
