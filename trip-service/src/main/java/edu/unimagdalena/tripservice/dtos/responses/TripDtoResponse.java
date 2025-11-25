package edu.unimagdalena.tripservice.dtos.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripDtoResponse(
        Long tripId,
        Long driverId,
        String origin,
        String destination,
        LocalDateTime startTime,
        Long seatsAvailable,
        BigDecimal price,
        String status
) {
}
