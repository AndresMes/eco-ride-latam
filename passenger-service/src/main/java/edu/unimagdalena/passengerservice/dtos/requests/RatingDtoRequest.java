package edu.unimagdalena.passengerservice.dtos.requests;

import jakarta.validation.constraints.*;

public record RatingDtoRequest(
        @NotNull(message = "Score cannot be null")
        @Min(value = 1, message = "Score must be at least 1")
        @Max(value = 5, message = "Score must be at most 5")
        Integer score,

        @Size(max = 500, message = "Comment cannot exceed 500 characters")
        String comment,

        @NotNull(message = "Trip ID cannot be null")
        @Positive(message = "Trip ID must be positive")
        Long tripId,

        String fromSub,

        @NotNull(message = "to ID cannot be null")
        @Positive(message = "to ID must be positive")
        Long toId
) {
}
