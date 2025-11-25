package edu.unimagdalena.paymentservice.domain.model;

import edu.unimagdalena.paymentservice.domain.enums.PaymentProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "charge", indexes = {
        @Index(name = "idx_payment_intent_id", columnList = "payment_intent_id"),
        @Index(name = "idx_provider_ref", columnList = "provider_reference"),
        @Index(name = "idx_captured_at", columnList = "captured_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Charge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_intent_id", nullable = false)
    private PaymentIntent paymentIntent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private PaymentProvider provider = PaymentProvider.STRIPE_MOCK;

    @Column(name = "provider_reference", unique = true, length = 100)
    private String providerReference;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "authorization_code", length = 50)
    private String authorizationCode;

    @Column(name = "captured_at")
    private LocalDateTime capturedAt;

    @Column(length = 500)
    private String description;

    @Column(name = "provider_metadata", columnDefinition = "TEXT")
    private String providerMetadata;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (capturedAt == null) {
            capturedAt = LocalDateTime.now();
        }
    }

    // Business methods
    public boolean isRefundable() {
        return capturedAt != null &&
                paymentIntent != null &&
                paymentIntent.getStatus() == edu.unimagdalena.paymentservice.domain.enums.PaymentStatus.CAPTURED;
    }

    public void markAsCaptured() {
        if (this.capturedAt == null) {
            this.capturedAt = LocalDateTime.now();
        }
    }
}