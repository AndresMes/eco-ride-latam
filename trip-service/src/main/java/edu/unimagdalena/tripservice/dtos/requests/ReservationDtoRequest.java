package edu.unimagdalena.tripservice.dtos.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;



public record ReservationDtoRequest(
        @NotNull @Positive Long passengerId
) {}


