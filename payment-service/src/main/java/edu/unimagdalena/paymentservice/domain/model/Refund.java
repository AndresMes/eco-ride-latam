package edu.unimagdalena.paymentservice.domain.model;

import edu.unimagdalena.paymentservice.domain.enums.RefundReason;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "refund", indexes = {
        @Index(name = "idx_charge_id", columnList = "charge_id"),
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_reason", columnList = "reason")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_id", nullable = false)
    private Charge charge;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private RefundReason reason = RefundReason.REQUESTED_BY_CUSTOMER;

    @Column(name = "reason_details", length = 500)
    private String reasonDetails;

    @Column(name = "provider_reference", unique = true, length = 100)
    private String providerReference;

    @Column(name = "idempotency_key", unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "provider_metadata", columnDefinition = "TEXT")
    private String providerMetadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (processedAt == null) {
            processedAt = LocalDateTime.now();
        }
    }

    // Business methods
    public boolean isPartialRefund() {
        return this.amount.compareTo(charge.getAmount()) < 0;
    }

    public boolean isFullRefund() {
        return this.amount.compareTo(charge.getAmount()) == 0;
    }

    public void markAsProcessed() {
        if (this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
    }
}