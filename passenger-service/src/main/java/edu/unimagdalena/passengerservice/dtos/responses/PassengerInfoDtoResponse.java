package edu.unimagdalena.passengerservice.dtos.responses;

public record PassengerInfoDtoResponse(
        Long passengerId,
        String name,
        String email,
        String keycloakSub,
        Double ratingAvg
) {
}
