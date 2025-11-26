package edu.unimagdalena.paymentservice.entities;

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
@Table("refunds")
public class Refund {

    @Id
    @Column("refund_id")
    private Long refundId;

    @Column("charge_id")
    private Long chargeId;

    @Column("amount")
    private BigDecimal amount;

    @Column("reason")
    private String reason;

    @Column("created_at")
    private LocalDateTime createdAt;
}
