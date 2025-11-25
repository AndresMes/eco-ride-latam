package edu.unimagdalena.paymentservice.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento publicado por TripService cuando se solicita una reserva
 * PaymentService escucha este evento para iniciar el proceso de pago
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequestedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long reservationId;
    private Long tripId;
    private Long passengerId;
    private BigDecimal amount;
    private String currency;
    private String correlationId;
    private LocalDateTime timestamp;

    // Metadata adicional
    private String passengerEmail;
    private String tripDescription;
}