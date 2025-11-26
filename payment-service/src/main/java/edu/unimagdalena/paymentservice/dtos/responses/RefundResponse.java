package edu.unimagdalena.paymentservice.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefundResponse(
        Long refundId,
        Long chargeId,
        BigDecimal amount,
        String reason,
        LocalDateTime createdAt
) {}
