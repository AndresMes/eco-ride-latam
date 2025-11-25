package edu.unimagdalena.passengerservice.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PassengerDtoRequest(
        @NotNull(message = "Name cannot be null") @NotBlank(message = "Name cannot be blank")
        String name,
        @NotNull(message = "Email cannot be null") @NotBlank(message = "Email cannot be blank")
        String email
) {
}
