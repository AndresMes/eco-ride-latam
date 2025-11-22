package edu.unimagdalena.passengerservice.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DriverDtoRequest(
        @NotBlank(message = "license cannot be blank") @NotNull(message = "license cannot be null")
        String licenseNo,
        @NotBlank(message = "car plate cannot be blank") @NotNull(message = "car plate cannot be null")
        String carPlate,
        @Positive(message = "Passenger ID cannot be negative") @NotNull
        Long passengerId
) {
}
