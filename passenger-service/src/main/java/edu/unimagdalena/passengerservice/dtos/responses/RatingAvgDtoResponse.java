package edu.unimagdalena.passengerservice.dtos.responses;

import lombok.Builder;

@Builder
public record RatingAvgDtoResponse(
        Double rating
) {
}
