package edu.unimagdalena.tripservice.services;

import edu.unimagdalena.tripservice.dtos.requests.ReservationDtoRequest;
import edu.unimagdalena.tripservice.dtos.responses.ReservationCreatedDtoResponse;
import edu.unimagdalena.tripservice.dtos.responses.ReservationDtoResponse;
import jakarta.annotation.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReservationService {

    Mono<ReservationCreatedDtoResponse> createReservation(Long tripId, ReservationDtoRequest dtoRequest, @Nullable String authorizationHeader);

    Mono<ReservationDtoResponse> getReservationById(Long reservationId);

    Flux<ReservationDtoResponse> getReservationsByPassenger(Long passengerId);

    Flux<ReservationDtoResponse> getReservationsByTrip(Long tripId);

    Mono<Boolean> existsByTripAndPassengerId(Long tripId, Long passengerId);

    Mono<Void> confirmReservation(Long reservationId, String paymentIntentId);

    Mono<Void> cancelReservation(Long reservationId, String reason);

    Mono<Boolean> checkAvailability(Long tripId);
}
