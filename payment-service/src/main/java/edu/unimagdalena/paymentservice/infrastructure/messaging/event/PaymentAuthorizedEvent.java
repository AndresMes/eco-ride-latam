package edu.unimagdalena.paymentservice.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento publicado por PaymentService cuando el pago es autorizado exitosamente
 * TripService escucha este evento para confirmar la reserva
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAuthorizedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long reservationId;
    private Long paymentIntentId;
    private Long chargeId;
    private BigDecimal amount;
    private String currency;
    private String correlationId;
    private LocalDateTime timestamp;
    private String authorizationCode;
    private String providerReference;
}