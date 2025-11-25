package edu.unimagdalena.paymentservice.domain.model;

import edu.unimagdalena.paymentservice.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreatedDate;
import org.hibernate.annotations.UpdatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_intent", indexes = {
        @Index(name = "idx_reservation_id", columnList = "reservation_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIntent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_id", nullable = false, unique = true)
    private Long reservationId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.REQUIRES_ACTION;

    @Column(name = "idempotency_key", unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(length = 500)
    private String description;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdatedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business methods
    public void authorize() {
        if (this.status != PaymentStatus.REQUIRES_ACTION) {
            throw new IllegalStateException("Cannot authorize payment in status: " + this.status);
        }
        this.status = PaymentStatus.AUTHORIZED;
    }

    public void capture() {
        if (this.status != PaymentStatus.AUTHORIZED) {
            throw new IllegalStateException("Cannot capture payment in status: " + this.status);
        }
        this.status = PaymentStatus.CAPTURED;
    }

    public void fail(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    public void cancel() {
        if (this.status == PaymentStatus.CAPTURED) {
            throw new IllegalStateException("Cannot cancel a captured payment. Use refund instead.");
        }
        this.status = PaymentStatus.CANCELLED;
    }

    public boolean isProcessable() {
        return this.status == PaymentStatus.REQUIRES_ACTION ||
                this.status == PaymentStatus.AUTHORIZED;
    }

    public boolean isFinal() {
        return this.status == PaymentStatus.CAPTURED ||
                this.status == PaymentStatus.FAILED ||
                this.status == PaymentStatus.CANCELLED;
    }
}