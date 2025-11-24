package edu.unimagdalena.passengerservice.services;

import edu.unimagdalena.passengerservice.dtos.requests.RatingDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.RatingAvgDtoResponse;
import edu.unimagdalena.passengerservice.dtos.responses.RatingDtoResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RatingService {

    Mono<RatingDtoResponse> toRateDriver(RatingDtoRequest ratingDtoRequest);
    Flux<RatingDtoResponse> findRatingsByDriver(Long driverId);
    Flux<RatingDtoResponse> findRatingsByTrip(Long tripId);
    Mono<RatingAvgDtoResponse> calculateRatingDriver(Long driverId);
}