package edu.unimagdalena.paymentservice.dtos.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentIntentRequest(
        @NotNull(message = "Reservation ID cannot be null")
        @Positive(message = "Reservation ID must be positive")
        Long reservationId,

        @NotNull(message = "Amount cannot be null")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount
) {}
