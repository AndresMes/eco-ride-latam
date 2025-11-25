package edu.unimagdalena.passengerservice.dtos.trip;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TripDto(
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
