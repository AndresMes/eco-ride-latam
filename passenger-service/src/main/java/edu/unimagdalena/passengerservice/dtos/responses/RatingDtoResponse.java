package edu.unimagdalena.passengerservice.dtos.responses;

import java.time.LocalDateTime;

public record RatingDtoResponse(
        Long ratingId,
        Integer score,
        String comment,
        Long tripId,
        String fromSub,
        Long toId,
        LocalDateTime createdAt
) {
}
