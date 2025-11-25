package edu.unimagdalena.paymentservice.application.dto.response;

import edu.unimagdalena.paymentservice.domain.enums.PaymentProvider;
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
public class ChargeResponse {

    private Long id;
    private Long paymentIntentId;
    private PaymentProvider provider;
    private String providerReference;
    private BigDecimal amount;
    private String currency;
    private String authorizationCode;
    private LocalDateTime capturedAt;
    private String description;
    private LocalDateTime createdAt;
}