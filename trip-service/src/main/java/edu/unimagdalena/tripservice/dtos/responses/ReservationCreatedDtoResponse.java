package edu.unimagdalena.tripservice.dtos.responses;

import edu.unimagdalena.tripservice.enums.StatusReservation;

import java.time.LocalDateTime;

public record ReservationCreatedDtoResponse(
        Long reservationId,
        Long tripId,
        Long passengerId,
        StatusReservation status,
        LocalDateTime createdAt
) {}

