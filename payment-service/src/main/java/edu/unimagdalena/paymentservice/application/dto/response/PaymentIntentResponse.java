package edu.unimagdalena.paymentservice.application.dto.response;

import edu.unimagdalena.paymentservice.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIntentResponse {

    private Long id;
    private Long reservationId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String description;
    private String failureReason;
    private String correlationId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}