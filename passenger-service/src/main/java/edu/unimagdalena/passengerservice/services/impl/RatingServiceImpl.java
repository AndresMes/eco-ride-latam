package edu.unimagdalena.passengerservice.services.impl;

import edu.unimagdalena.passengerservice.clients.TripServiceClient;
import edu.unimagdalena.passengerservice.dtos.requests.RatingDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.RatingAvgDtoResponse;
import edu.unimagdalena.passengerservice.dtos.responses.RatingDtoResponse;
import edu.unimagdalena.passengerservice.dtos.trip.TripDto;
import edu.unimagdalena.passengerservice.entities.DriverProfile;
import edu.unimagdalena.passengerservice.entities.Passenger;
import edu.unimagdalena.passengerservice.entities.Rating;
import edu.unimagdalena.passengerservice.exceptions.DuplicateRatingException;
import edu.unimagdalena.passengerservice.exceptions.ExternalServiceException;
import edu.unimagdalena.passengerservice.exceptions.InvalidTripException;
import edu.unimagdalena.passengerservice.exceptions.UnauthorizedRatingException;
import edu.unimagdalena.passengerservice.exceptions.notFound.DriverNotFoundException;
import edu.unimagdalena.passengerservice.exceptions.notFound.PassengerNotFoundException;
import edu.unimagdalena.passengerservice.mappers.RatingMapper;
import edu.unimagdalena.passengerservice.repositories.DriverRepository;
import edu.unimagdalena.passengerservice.repositories.PassengerRepository;
import edu.unimagdalena.passengerservice.repositories.RatingRepository;
import edu.unimagdalena.passengerservice.services.RatingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final RatingMapper ratingMapper;
    private final TripServiceClient tripClient;

    @Override
    @Transactional
    public RatingDtoResponse toRateDriver(RatingDtoRequest ratingDtoRequest) {

        Passenger passenger = passengerRepository.findByKeycloakSub(ratingDtoRequest.fromSub())
                .orElseThrow(() -> new PassengerNotFoundException("Passenger not found"));

        DriverProfile driver = driverRepository.findById(ratingDtoRequest.toId())
                .orElseThrow(() -> new DriverNotFoundException("Driver with ID: "+ratingDtoRequest.toId()+" not found"));

        if (passenger.getPassengerId().equals(driver.getDriverId())) {
            throw new UnauthorizedRatingException("Passenger cannot rate themselves");
        }

        //Validaciones con TripService
        TripDto trip;
        Boolean hasReservation;

        try{
            trip = tripClient.getTripById(ratingDtoRequest.tripId());
            hasReservation = tripClient.existsByTripAndPassenger(ratingDtoRequest.tripId(), passenger.getPassengerId());

        }catch(feign.FeignException.NotFound ex){
            throw new InvalidTripException("Trip with ID: "+ratingDtoRequest.tripId()+" not found");
        }catch(feign.FeignException ex){
            throw new ExternalServiceException("Trip service error: "+ ex.getMessage());
        }

        if(trip == null){
            throw new InvalidTripException("Trip not found");
        }

        //Validate driver does match
        if (!trip.driverId().equals(ratingDtoRequest.toId())) {
            throw new InvalidTripException("Driver does not match the trip's driver");
        }

        //Validate passenger did participate in the trip
        if(hasReservation == null || !hasReservation){
            throw new UnauthorizedRatingException("Passenger did not participate in this trip");
        }

        //Validate statusTrip is FINISHED
        if (!"FINISHED".equalsIgnoreCase(trip.status())) {
            throw new InvalidTripException("Trip is not finished; rating not allowed");
        }

        //Validate passenger haven't rated yet
        if (ratingRepository.existsByTripIdAndFrom_PassengerId(
                ratingDtoRequest.tripId(), passenger.getPassengerId())) {
            throw new DuplicateRatingException(
                    "You already rated this trip");
        }


        Rating rating = ratingMapper.toEntity(ratingDtoRequest);

        rating.setFrom(passenger);
        rating.setTo(driver);

        if (rating.getComment() != null) {
            String trimmed = rating.getComment().trim();
            if (trimmed.length() > 500) {
                trimmed = trimmed.substring(0, 500);
            }
            rating.setComment(trimmed);
        }

        return ratingMapper.toRatingDtoResponse(ratingRepository.save(rating));

    }

    @Override
    public List<RatingDtoResponse> findRatingsByDriver(Long driverId) {
        return ratingRepository.findByTo_DriverId(driverId)
                .stream()
                .map(ratingMapper::toRatingDtoResponse)
                .toList();
    }

    @Override
    public List<RatingDtoResponse> findRatingsByTrip(Long tripId) {
        return ratingRepository.findByTripId(tripId)
                .stream()
                .map(ratingMapper::toRatingDtoResponse)
                .toList();
    }

    @Override
    public RatingAvgDtoResponse calculateRatingDriver(Long driverId) {

        if (!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException("Driver with ID: " + driverId + " not found");
        }


        Double rating = ratingRepository.calculateAverageRating(driverId);
        return new RatingAvgDtoResponse(rating != null ? rating : 0.0);
    }
}
