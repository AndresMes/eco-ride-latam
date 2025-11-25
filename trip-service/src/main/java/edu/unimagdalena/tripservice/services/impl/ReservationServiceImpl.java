package edu.unimagdalena.tripservice.services.impl;

import edu.unimagdalena.tripservice.dtos.requests.ReservationDtoRequest;
import edu.unimagdalena.tripservice.dtos.responses.ReservationCreatedDtoResponse;
import edu.unimagdalena.tripservice.dtos.responses.ReservationDtoResponse;
import edu.unimagdalena.tripservice.entities.Reservation;
import edu.unimagdalena.tripservice.entities.Trip;
import edu.unimagdalena.tripservice.enums.StatusReservation;
import edu.unimagdalena.tripservice.events.ReservationCancelledEvent;
import edu.unimagdalena.tripservice.events.ReservationConfirmedEvent;
import edu.unimagdalena.tripservice.events.ReservationRequestedEvent;
import edu.unimagdalena.tripservice.events.publisher.NotificationEventPublisher;
import edu.unimagdalena.tripservice.events.publisher.ReservationEventPublisher;
import edu.unimagdalena.tripservice.exceptions.ReservationStatusNotAllowedToChangeException;
import edu.unimagdalena.tripservice.exceptions.notFound.ReservationNotFoundException;
import edu.unimagdalena.tripservice.exceptions.notFound.TripNotFoundException;
import edu.unimagdalena.tripservice.mappers.ReservationMapper;
import edu.unimagdalena.tripservice.repositories.ReservationRepository;
import edu.unimagdalena.tripservice.repositories.TripRepository;
import edu.unimagdalena.tripservice.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final TripRepository tripRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final ReservationEventPublisher reservationEventPublisher;
    private final NotificationEventPublisher notificationEventPublisher;

    @Override
    @Transactional
    public Mono<ReservationCreatedDtoResponse> createReservation(Long tripId, ReservationDtoRequest dtoRequest) {
        return tripRepository.findById(tripId)
                .switchIfEmpty(Mono.error(new TripNotFoundException("Trip with ID: " + tripId + " not found")))
                .flatMap(trip -> {
                    // TODO: Implement validation of passengerId (same place as original comment)

                    Reservation reservation = reservationMapper.toEntity(dtoRequest);
                    reservation.setStatus(StatusReservation.PENDING);
                    reservation.setTripId(trip.getTripId());
                    reservation.setCreatedAt(LocalDateTime.now());

                    return reservationRepository.save(reservation)
                            .doOnSuccess(saved -> publishReservationRequestedEvent(saved, trip))
                            .map(reservationMapper::toCreatedDtoResponse);
                });
    }

    @Override
    public Mono<ReservationDtoResponse> getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException("Reservation with ID: " + reservationId + " not found")))
                .map(reservationMapper::toDtoResponse);
    }

    @Override
    public Flux<ReservationDtoResponse> getReservationsByPassenger(Long passengerId) {
        return reservationRepository.findAllByPassengerId(passengerId)
                .map(reservationMapper::toDtoResponse);
    }

    @Override
    public Flux<ReservationDtoResponse> getReservationsByTrip(Long tripId) {
        return reservationRepository.findAllByTripId(tripId)
                .map(reservationMapper::toDtoResponse);
    }

    @Override
    public Mono<Boolean> existsByTripAndPassengerId(Long tripId, Long passengerId) {
        return reservationRepository.existsByTripIdAndPassengerId(tripId, passengerId);
    }

    @Override
    @Transactional
    public Mono<Void> confirmReservation(Long reservationId, String paymentIntentId) {
        return reservationRepository.findById(reservationId)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException("Reservation with ID: " + reservationId + " not found")))
                .flatMap(reservation -> {
                    if (reservation.getStatus() == StatusReservation.CONFIRMED) {
                        return Mono.empty();
                    }

                    if (!reservation.getStatus().equals(StatusReservation.PENDING)) {
                        return Mono.error(new ReservationStatusNotAllowedToChangeException(
                                "Reservation with ID: " + reservationId + " is not PENDING. Current status: " + reservation.getStatus()
                        ));
                    }

                    reservation.setStatus(StatusReservation.CONFIRMED);
                    // Si quieres guardar paymentIntentId, añádelo al DTO/entidad y here setPaymentIntentId(paymentIntentId)

                    return reservationRepository.save(reservation)
                            .doOnSuccess(this::publishReservationConfirmedEvent)
                            .then();
                });
    }

    @Override
    @Transactional
    public Mono<Void> cancelReservation(Long reservationId, String reason) {
        return reservationRepository.findById(reservationId)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException("Reservation with ID: " + reservationId + " not found")))
                .flatMap(reservation -> {
                    if (reservation.getStatus() == StatusReservation.CANCELLED) {
                        return Mono.empty();
                    }

                    return tripRepository.findById(reservation.getTripId())
                            .switchIfEmpty(Mono.error(new TripNotFoundException("Trip with ID: " + reservation.getTripId() + " not found")))
                            .flatMap(trip -> {
                                trip.restoreSeat();
                                reservation.setStatus(StatusReservation.CANCELLED);

                                return reservationRepository.save(reservation)
                                        .flatMap(savedRes ->
                                                tripRepository.save(trip)
                                                        .doOnSuccess(savedTrip -> publishReservationCancelledEvent(savedRes, reason))
                                        )
                                        .then();
                            });
                });
    }

    @Override
    public Mono<Boolean> checkAvailability(Long tripId) {
        return tripRepository.findById(tripId)
                .map(trip -> trip.getSeatsAvailable() != null && trip.getSeatsAvailable() > 0)
                .defaultIfEmpty(false);
    }

    private void publishReservationRequestedEvent(Reservation reservation, Trip trip) {
        ReservationRequestedEvent event = ReservationRequestedEvent.builder()
                .reservationId(reservation.getReservationId())
                .tripId(trip.getTripId())
                .passengerId(reservation.getPassengerId())
                .amount(trip.getPrice())
                .timestamp(LocalDateTime.now())
                .build();

        reservationEventPublisher.publishReservationRequested(event);
    }

    private void publishReservationCancelledEvent(Reservation reservation, String reasonP) {
        ReservationCancelledEvent event = ReservationCancelledEvent.builder()
                .reservationId(reservation.getReservationId())
                .reason(reasonP)
                .timestamp(LocalDateTime.now())
                .build();

        notificationEventPublisher.publishReservationCancelled(event);
    }

    private void publishReservationConfirmedEvent(Reservation reservation) {
        ReservationConfirmedEvent event = ReservationConfirmedEvent.builder()
                .reservationId(reservation.getReservationId())
                .timestamp(LocalDateTime.now())
                .build();

        notificationEventPublisher.publishReservationConfirmed(event);
    }
}
