package edu.unimagdalena.tripservice.dtos.requests;

import edu.unimagdalena.tripservice.enums.StatusTrip;
import jakarta.validation.constraints.NotNull;

public record TripDtoUpdateStatus(
        @NotNull(message = "Status cannot be null")
        StatusTrip newStatus
) {
}
