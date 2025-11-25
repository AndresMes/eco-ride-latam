package edu.unimagdalena.passengerservice.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DriverDtoRequest(
        @NotBlank(message = "License cannot be blank")
        @NotNull(message = "License cannot be null")
        String licenseNo,

        @NotBlank(message = "Car plate cannot be blank")
        @NotNull(message = "Car plate cannot be null")
        String carPlate
) {
}
