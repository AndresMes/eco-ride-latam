package edu.unimagdalena.passengerservice.services;

import edu.unimagdalena.passengerservice.dtos.requests.RatingDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.RatingAvgDtoResponse;
import edu.unimagdalena.passengerservice.dtos.responses.RatingDtoResponse;

import java.util.List;

public interface RatingService {

    RatingDtoResponse toRateDriver(RatingDtoRequest ratingDtoRequest);
    List<RatingDtoResponse> findRatingsByDriver(Long driverId);
    List<RatingDtoResponse> findRatingsByTrip(Long tripId);
    RatingAvgDtoResponse calculateRatingDriver(Long driverId);
}
