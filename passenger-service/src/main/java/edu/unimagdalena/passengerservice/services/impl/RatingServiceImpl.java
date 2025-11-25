package edu.unimagdalena.passengerservice.services.impl;

import edu.unimagdalena.passengerservice.clients.TripServiceClient;
import edu.unimagdalena.passengerservice.dtos.requests.RatingDtoRequest;
import edu.unimagdalena.passengerservice.dtos.responses.RatingAvgDtoResponse;
import edu.unimagdalena.passengerservice.dtos.responses.RatingDtoResponse;
import edu.unimagdalena.passengerservice.dtos.trip.TripDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final RatingMapper ratingMapper;
    private final TripServiceClient tripClient;

    @Override
    public Mono<RatingDtoResponse> toRateDriver(RatingDtoRequest ratingDtoRequest) {
        return passengerRepository.findByKeycloakSub(ratingDtoRequest.fromSub())
                .switchIfEmpty(Mono.error(new PassengerNotFoundException("Passenger not found")))
                .zipWith(driverRepository.findById(ratingDtoRequest.toId())
                        .switchIfEmpty(Mono.error(new DriverNotFoundException(
                                "Driver with ID: " + ratingDtoRequest.toId() + " not found"))))
                .flatMap(tuple -> {
                    var passenger = tuple.getT1();
                    var driver = tuple.getT2();

                    if (passenger.getPassengerId().equals(driver.getDriverId())) {
                        return Mono.error(new UnauthorizedRatingException("Passenger cannot rate themselves"));
                    }

                    // Validaciones con TripService
                    return Mono.fromCallable(() -> {
                                try {
                                    TripDto trip = tripClient.getTripById(ratingDtoRequest.tripId());
                                    Boolean hasReservation = tripClient.existsByTripAndPassenger(ratingDtoRequest.tripId(), passenger.getPassengerId());
                                    return new Object[] { trip, hasReservation };
                                } catch (feign.FeignException.NotFound ex) {
                                    throw new InvalidTripException("Trip with ID: " + ratingDtoRequest.tripId() + " not found");
                                } catch (feign.FeignException ex) {
                                    throw new ExternalServiceException("Trip service error: " + ex.getMessage());
                                }
                            })
                            .flatMap(result -> {
                                TripDto trip = (TripDto) result[0];
                                Boolean hasReservation = (Boolean) result[1];

                                if (trip == null) {
                                    return Mono.error(new InvalidTripException("Trip not found"));
                                }

                                if (!trip.driverId().equals(ratingDtoRequest.toId())) {
                                    return Mono.error(new InvalidTripException("Driver does not match the trip's driver"));
                                }

                                if (hasReservation == null || !hasReservation) {
                                    return Mono.error(new UnauthorizedRatingException("Passenger did not participate in this trip"));
                                }

                                if (!"FINISHED".equalsIgnoreCase(trip.status())) {
                                    return Mono.error(new InvalidTripException("Trip is not finished; rating not allowed"));
                                }

                                return ratingRepository.existsByTripIdAndFromId(ratingDtoRequest.tripId(), passenger.getPassengerId())
                                        .flatMap(exists -> {
                                            if (exists) {
                                                return Mono.error(new DuplicateRatingException("You already rated this trip"));
                                            }

                                            Rating rating = ratingMapper.toEntity(ratingDtoRequest);
                                            rating.setFromId(passenger.getPassengerId());
                                            rating.setToId(driver.getDriverId());
                                            rating.setCreatedAt(LocalDateTime.now());

                                            if (rating.getComment() != null) {
                                                String trimmed = rating.getComment().trim();
                                                if (trimmed.length() > 500) {
                                                    trimmed = trimmed.substring(0, 500);
                                                }
                                                rating.setComment(trimmed);
                                            }

                                            return ratingRepository.save(rating);
                                        });
                            });
                })
                .map(ratingMapper::toRatingDtoResponse);
    }

    @Override
    public Flux<RatingDtoResponse> findRatingsByDriver(Long driverId) {
        return ratingRepository.findByToId(driverId)
                .map(ratingMapper::toRatingDtoResponse);
    }

    @Override
    public Flux<RatingDtoResponse> findRatingsByTrip(Long tripId) {
        return ratingRepository.findByTripId(tripId)
                .map(ratingMapper::toRatingDtoResponse);
    }

    @Override
    public Mono<RatingAvgDtoResponse> calculateRatingDriver(Long driverId) {
        return driverRepository.existsById(driverId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new DriverNotFoundException("Driver with ID: " + driverId + " not found"));
                    }
                    return ratingRepository.calculateAverageRating(driverId)
                            .defaultIfEmpty(0.0)
                            .map(RatingAvgDtoResponse::new);
                });
    }
}