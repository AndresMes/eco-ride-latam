package edu.unimagdalena.tripservice.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;



public record ReservationDtoRequest(
        @NotNull(message = "Passenger id cannot be null") @Positive(message = "Passenger ID cannot be negative or zero")
        Long passengerId,
        @Positive(message = "Trip ID cannot be negative or zero")
        Long tripId
) {
}
