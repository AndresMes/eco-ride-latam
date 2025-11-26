package edu.unimagdalena.paymentservice.dtos.responses;

import edu.unimagdalena.paymentservice.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentIntentResponse(
        Long paymentIntentId,
        Long reservationId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        LocalDateTime createdAt
) {}
