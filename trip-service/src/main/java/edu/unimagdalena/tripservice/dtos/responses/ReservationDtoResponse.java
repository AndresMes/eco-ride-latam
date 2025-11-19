package edu.unimagdalena.tripservice.dtos.responses;

import edu.unimagdalena.tripservice.enums.StatusReservation;

import java.time.LocalDateTime;

public record ReservationDtoResponse(
        Long reservationId,
        Long passengerId,
        StatusReservation status,
        LocalDateTime createdAt,
        Long tripId
) {
}
