package edu.unimagdalena.tripservice.services.impl;

import edu.unimagdalena.tripservice.clients.PassengerClient;
import edu.unimagdalena.tripservice.dtos.requests.ReservationDtoRequest;
import edu.unimagdalena.tripservice.dtos.requests.TripDtoRequest;
import edu.unimagdalena.tripservice.dtos.responses.ReservationCreatedDtoResponse;
import edu.unimagdalena.tripservice.dtos.responses.TripDtoResponse;
import edu.unimagdalena.tripservice.dtos.requests.TripDtoUpdateStatus;
import edu.unimagdalena.tripservice.entities.Trip;
import edu.unimagdalena.tripservice.enums.StatusTrip;
import edu.unimagdalena.tripservice.events.TripCompletedEvent;
import edu.unimagdalena.tripservice.events.publisher.TripEventPublisher;
import edu.unimagdalena.tripservice.exceptions.ReservationAlreadyExistsException;
import edu.unimagdalena.tripservice.exceptions.TripStatusUpdateNotAllowedException;
import edu.unimagdalena.tripservice.exceptions.notFound.DriverNotFoundException;
import edu.unimagdalena.tripservice.exceptions.notFound.TripNotFoundException;
import edu.unimagdalena.tripservice.mappers.TripMapper;
import edu.unimagdalena.tripservice.repositories.ReservationRepository;
import edu.unimagdalena.tripservice.repositories.TripRepository;
import edu.unimagdalena.tripservice.services.ReservationService;
import edu.unimagdalena.tripservice.services.TripService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final PassengerClient passengerClient;
    private final TripEventPublisher tripEventPublisher;

    @Override
    public Mono<TripDtoResponse> getTripById(Long id) {
        return tripRepository.findById(id)
                .switchIfEmpty(Mono.error(new TripNotFoundException("Trip with ID: " + id + " not found")))
                .map(tripMapper::toDtoResponse);
    }

    @Override
    public Flux<TripDtoResponse> getAllTrips() {
        return tripRepository.findAll()
                .map(tripMapper::toDtoResponse);
    }

    @Override
    public Flux<TripDtoResponse> searchTrips(String origin,
                                             String destination,
                                             LocalDateTime from,
                                             LocalDateTime to) {
        return tripRepository.searchTrips(origin, destination, from, to)
                .map(tripMapper::toDtoResponse);
    }

    @Override
    @Transactional
    public Mono<TripDtoResponse> createTrip(TripDtoRequest dtoRequest, @Nullable String authorizationHeader) {

        Trip tripEntity = tripMapper.toEntity(dtoRequest);
        tripEntity.setStatus(StatusTrip.SCHEDULED);

        return tripRepository.save(tripEntity)
                .flatMap(trip ->
                        passengerClient.findDriverById(dtoRequest.driverId(), authorizationHeader)
                                .switchIfEmpty(Mono.error(
                                        new DriverNotFoundException("Driver with ID " + dtoRequest.driverId() + " not found")
                                ))
                                .thenReturn(trip)
                )
                .map(tripMapper::toDtoResponse);
    }


    @Override
    @Transactional
    public Mono<TripDtoResponse> updateTrip(Long id, TripDtoRequest dtoRequest) {
        return tripRepository.findById(id)
                .switchIfEmpty(Mono.error(new TripNotFoundException("Trip with ID: " + id + " not found")))
                .flatMap(trip -> {
                    tripMapper.updateTripFromDto(dtoRequest, trip);
                    return tripRepository.save(trip);
                })
                .map(tripMapper::toDtoResponse);
    }

    @Override
    @Transactional
    public Mono<TripDtoResponse> updateTripStatus(Long id, TripDtoUpdateStatus newStatus) {
        return tripRepository.findById(id)
                .switchIfEmpty(Mono.error(new TripNotFoundException("Trip with ID: " + id + " not found")))
                .flatMap(trip -> {
                    if (trip.getStatus().equals(StatusTrip.CANCELLED) || trip.getStatus().equals(StatusTrip.FINISHED)) {
                        return Mono.error(new TripStatusUpdateNotAllowedException(
                                "Cannot update status of a trip that is already " + trip.getStatus()
                        ));
                    }

                    trip.setStatus(newStatus.newStatus());
                    return tripRepository.save(trip)
                            .flatMap(saved -> {
                                if (saved.getStatus().equals(StatusTrip.FINISHED)) {
                                    publishTripCompletedEvent(saved);
                                }
                                return Mono.just(saved);
                            });
                })
                .map(tripMapper::toDtoResponse);
    }

    @Override
    @Transactional
    public Mono<ReservationCreatedDtoResponse> createReservationInTrip(Long tripId, ReservationDtoRequest reservationDtoRequest, @Nullable String authorizationHeader) {
        return tripRepository.findById(tripId)
                .switchIfEmpty(Mono.error(new TripNotFoundException("Trip with ID: " + tripId + " not found")))
                .flatMap(trip -> {

                    trip.reserveSeat();

                    return tripRepository.save(trip)
                            .flatMap(savedTrip ->
                                    reservationRepository.existsByTripIdAndPassengerId(tripId, reservationDtoRequest.passengerId())
                                            .flatMap(exists -> {
                                                if (Boolean.TRUE.equals(exists)) {
                                                    return Mono.error(new ReservationAlreadyExistsException("Passenger already reserved this trip"));
                                                }
                                                return reservationService.createReservation(tripId, reservationDtoRequest, authorizationHeader);
                                            })
                            );
                });
    }

    private void publishTripCompletedEvent(Trip trip) {
        TripCompletedEvent event = TripCompletedEvent.builder()
                .tripId(trip.getTripId())
                .timestamp(LocalDateTime.now())
                .build();

        tripEventPublisher.publishTripCompleted(event);
    }
}
