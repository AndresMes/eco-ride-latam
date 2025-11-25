package edu.unimagdalena.paymentservice.domain.repository;

import edu.unimagdalena.paymentservice.domain.enums.PaymentStatus;
import edu.unimagdalena.paymentservice.domain.model.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {

    /**
     * Encuentra un PaymentIntent por su reservationId
     */
    Optional<PaymentIntent> findByReservationId(Long reservationId);

    /**
     * Encuentra un PaymentIntent por su idempotency key
     * (importante para evitar pagos duplicados)
     */
    Optional<PaymentIntent> findByIdempotencyKey(String idempotencyKey);

    /**
     * Encuentra todos los PaymentIntents por estado
     */
    List<PaymentIntent> findByStatus(PaymentStatus status);

    /**
     * Encuentra PaymentIntents por correlationId
     * (útil para rastrear toda una transacción distribuida)
     */
    List<PaymentIntent> findByCorrelationId(String correlationId);

    /**
     * Encuentra PaymentIntents pendientes de procesamiento
     * (útil para jobs de retry o limpieza)
     */
    @Query("SELECT pi FROM PaymentIntent pi WHERE pi.status = 'REQUIRES_ACTION' " +
            "AND pi.createdAt < :beforeDate")
    List<PaymentIntent> findStalePendingPayments(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * Verifica si existe un pago para una reserva específica
     */
    boolean existsByReservationId(Long reservationId);

    /**
     * Encuentra PaymentIntents con estado y rango de fechas
     */
    @Query("SELECT pi FROM PaymentIntent pi WHERE pi.status = :status " +
            "AND pi.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY pi.createdAt DESC")
    List<PaymentIntent> findByStatusAndDateRange(
            @Param("status") PaymentStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Cuenta pagos por estado
     */
    long countByStatus(PaymentStatus status);
}