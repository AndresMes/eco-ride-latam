package edu.unimagdalena.tripservice.services;

import edu.unimagdalena.tripservice.dtos.requests.ReservationDtoRequest;
import edu.unimagdalena.tripservice.dtos.requests.TripDtoRequest;
import edu.unimagdalena.tripservice.dtos.requests.TripDtoUpdateStatus;
import edu.unimagdalena.tripservice.dtos.responses.ReservationCreatedDtoResponse;
import edu.unimagdalena.tripservice.dtos.responses.TripDtoResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface TripService {

    Mono<TripDtoResponse> getTripById(Long id);

    Flux<TripDtoResponse> getAllTrips();

    Flux<TripDtoResponse> searchTrips(
            String origin,
            String destination,
            LocalDateTime from,
            LocalDateTime to
    );

    Mono<TripDtoResponse> createTrip(TripDtoRequest dtoRequest);

    Mono<TripDtoResponse> updateTrip(Long id, TripDtoRequest dtoRequest);

    Mono<TripDtoResponse> updateTripStatus(Long id, TripDtoUpdateStatus newStatus);

    Mono<ReservationCreatedDtoResponse> createReservationInTrip(Long tripId, ReservationDtoRequest reservationDtoRequest);
}
