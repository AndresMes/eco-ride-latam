package edu.unimagdalena.paymentservice.domain.repository;

import edu.unimagdalena.paymentservice.domain.enums.PaymentProvider;
import edu.unimagdalena.paymentservice.domain.model.Charge;
import edu.unimagdalena.paymentservice.domain.model.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, Long> {

    /**
     * Encuentra un Charge por su provider reference
     * (útil para reconciliación con proveedores externos)
     */
    Optional<Charge> findByProviderReference(String providerReference);

    /**
     * Encuentra todos los Charges de un PaymentIntent específico
     */
    List<Charge> findByPaymentIntent(PaymentIntent paymentIntent);

    /**
     * Encuentra todos los Charges de un PaymentIntent por ID
     */
    List<Charge> findByPaymentIntentId(Long paymentIntentId);

    /**
     * Encuentra Charges por proveedor
     */
    List<Charge> findByProvider(PaymentProvider provider);

    /**
     * Encuentra Charges capturados en un rango de fechas
     */
    @Query("SELECT c FROM Charge c WHERE c.capturedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY c.capturedAt DESC")
    List<Charge> findByCapturedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Encuentra el último Charge de un PaymentIntent
     */
    @Query("SELECT c FROM Charge c WHERE c.paymentIntent.id = :paymentIntentId " +
            "ORDER BY c.createdAt DESC LIMIT 1")
    Optional<Charge> findLatestByPaymentIntentId(@Param("paymentIntentId") Long paymentIntentId);

    /**
     * Verifica si existe un Charge para un PaymentIntent
     */
    boolean existsByPaymentIntentId(Long paymentIntentId);

    /**
     * Cuenta Charges por proveedor
     */
    long countByProvider(PaymentProvider provider);
}