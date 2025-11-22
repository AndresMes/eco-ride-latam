package edu.unimagdalena.passengerservice.dtos.requests;

import jakarta.validation.constraints.Size;

public record DriverDtoUpdateRequest(
        @Size(min = 1, message = "license cannot be blank") String licenseNo,
        @Size(min = 1, message = "license cannot be blank") String carPlate
) {
}
