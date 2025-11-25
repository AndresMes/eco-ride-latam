package edu.unimagdalena.paymentservice.infrastructure.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Evento publicado por TripService cuando la reserva es cancelada
 * NotificationService escucha este evento para enviar notificaciones
 * PaymentService puede escucharlo si necesita hacer refunds
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationCancelledEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long reservationId;
    private Long tripId;
    private Long passengerId;
    private String reason;
    private String correlationId;
    private LocalDateTime timestamp;

    // Información para notificaciones
    private String passengerEmail;
    private String passengerName;
    private String cancellationReason;
}