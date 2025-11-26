package edu.unimagdalena.paymentservice.entities;

import edu.unimagdalena.paymentservice.enums.PaymentStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("payment_intents")
public class PaymentIntent {

    @Id
    @Column("payment_intent_id")
    private Long paymentIntentId;

    @Column("reservation_id")
    private Long reservationId;

    @Column("amount")
    private BigDecimal amount;

    @Column("currency")
    private String currency;

    @Column("status")
    private PaymentStatus status;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
