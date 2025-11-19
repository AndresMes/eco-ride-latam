package edu.unimagdalena.tripservice.dtos.requests;


import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripDtoRequest(
        @NotNull @Positive
        Long driverId,

        @NotBlank(message = "Origin cannot be blank")
        String origin,

        @NotBlank(message = "Destination cannot be blank")
        String destination,

        @FutureOrPresent(message = "Date cannot be in past")
        LocalDateTime startTime,

        @Positive(message = "number of seats cannot be negative or zero")
        Long seatsAvailable,

        @Positive(message = "Price cannot be negative")
        BigDecimal price
) {
}
