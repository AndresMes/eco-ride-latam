package edu.unimagdalena.paymentservice.entities;

import edu.unimagdalena.paymentservice.enums.PaymentProvider;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("charges")
public class Charge {

    @Id
    @Column("charge_id")
    private Long chargeId;

    @Column("payment_intent_id")
    private Long paymentIntentId;

    @Column("provider")
    private PaymentProvider provider;

    @Column("provider_ref")
    private String providerRef;

    @Column("captured_at")
    private LocalDateTime capturedAt;
}
