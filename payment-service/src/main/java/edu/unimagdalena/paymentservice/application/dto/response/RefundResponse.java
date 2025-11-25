package edu.unimagdalena.paymentservice.application.dto.response;

import edu.unimagdalena.paymentservice.domain.enums.RefundReason;
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
public class RefundResponse {

    private Long id;
    private Long chargeId;
    private BigDecimal amount;
    private String currency;
    private RefundReason reason;
    private String reasonDetails;
    private String providerReference;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
}