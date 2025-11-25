package edu.unimagdalena.paymentservice.application.dto.request;

import edu.unimagdalena.paymentservice.domain.enums.RefundReason;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequest {

    @NotNull(message = "Charge ID is required")
    @Positive(message = "Charge ID must be positive")
    private Long chargeId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Amount must have at most 2 decimal places")
    private BigDecimal amount;

    @NotNull(message = "Reason is required")
    private RefundReason reason;

    @Size(max = 500, message = "Reason details must not exceed 500 characters")
    private String reasonDetails;

    @Size(max = 100, message = "Idempotency key must not exceed 100 characters")
    private String idempotencyKey;
}