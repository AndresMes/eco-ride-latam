package edu.unimagdalena.paymentservice.dtos.responses;

import edu.unimagdalena.paymentservice.enums.PaymentProvider;

import java.time.LocalDateTime;

public record ChargeResponse(
        Long chargeId,
        Long paymentIntentId,
        PaymentProvider provider,
        String providerRef,
        LocalDateTime capturedAt
) {}
