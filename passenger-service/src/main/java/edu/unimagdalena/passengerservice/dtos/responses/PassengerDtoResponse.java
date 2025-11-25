package edu.unimagdalena.passengerservice.dtos.responses;

public record PassengerDtoResponse(
        Long passengerId,
        String name,
        String email,
        String keycloakSub
) {
}
