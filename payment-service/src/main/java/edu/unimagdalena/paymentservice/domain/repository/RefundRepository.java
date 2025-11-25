package edu.unimagdalena.paymentservice.domain.repository;

import edu.unimagdalena.paymentservice.domain.enums.RefundReason;
import edu.unimagdalena.paymentservice.domain.model.Charge;
import edu.unimagdalena.paymentservice.domain.model.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {

    /**
     * Encuentra un Refund por su provider reference
     */
    Optional<Refund> findByProviderReference(String providerReference);

    /**
     * Encuentra un Refund por su idempotency key
     */
    Optional<Refund> findByIdempotencyKey(String idempotencyKey);

    /**
     * Encuentra todos los Refunds de un Charge específico
     */
    List<Refund> findByCharge(Charge charge);

    /**
     * Encuentra todos los Refunds de un Charge por ID
     */
    List<Refund> findByChargeId(Long chargeId);

    /**
     * Encuentra Refunds por razón
     */
    List<Refund> findByReason(RefundReason reason);

    /**
     * Encuentra Refunds procesados en un rango de fechas
     */
    @Query("SELECT r FROM Refund r WHERE r.processedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY r.processedAt DESC")
    List<Refund> findByProcessedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Calcula el total reembolsado para un Charge específico
     */
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Refund r WHERE r.charge.id = :chargeId")
    BigDecimal calculateTotalRefundedForCharge(@Param("chargeId") Long chargeId);

    /**
     * Verifica si un Charge tiene refunds
     */
    boolean existsByChargeId(Long chargeId);

    /**
     * Encuentra refunds de compensación del Saga
     */
    @Query("SELECT r FROM Refund r WHERE r.reason = 'SAGA_COMPENSATION' " +
            "AND r.processedAt >= :sinceDate " +
            "ORDER BY r.processedAt DESC")
    List<Refund> findSagaCompensationRefunds(@Param("sinceDate") LocalDateTime sinceDate);

    /**
     * Cuenta refunds por razón
     */
    long countByReason(RefundReason reason);
}