package edu.unimagdalena.paymentservice.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento publicado por PaymentService cuando el pago falla
 * TripService escucha este evento para cancelar la reserva (compensación del Saga)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long reservationId;
    private Long paymentIntentId;
    private String reason;
    private String correlationId;
    private LocalDateTime timestamp;

    // Detalles adicionales del error
    private String errorCode;
    private String errorDetails;
}