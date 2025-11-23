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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    public ReservationCreatedDtoResponse createReservation(Long tripId, ReservationDtoRequest dtoRequest) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip with ID: "+tripId+" not found"));


        /*
            Implement validation of passengerId
        */

        Reservation reservation = reservationMapper.toEntity(dtoRequest);

        reservation.setStatus(StatusReservation.PENDING);
        reservation.setTrip(trip);
        reservation.setCreatedAt(LocalDateTime.now());

        Reservation saved = reservationRepository.save(reservation);

        publishReservationRequestedEvent(saved, trip);

        return reservationMapper.toCreatedDtoResponse(saved);

    }

    @Override
    public ReservationDtoResponse getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation with ID: " + reservationId + " not found"));

        return reservationMapper.toDtoResponse(reservation);
    }

    @Override
    public List<ReservationDtoResponse> getReservationsByPassenger(Long passengerId) {
        return reservationRepository.findAllByPassengerId(passengerId)
                .stream()
                .map(reservationMapper::toDtoResponse)
                .toList();
    }

    @Override
    public List<ReservationDtoResponse> getReservationsByTrip(Long tripId) {
        return reservationRepository.findAllByTrip_TripId(tripId)
                .stream()
                .map(reservationMapper::toDtoResponse)
                .toList();
    }

    @Override
    public boolean existsByTripAndPassengerId(Long tripId, Long passengerId) {
        return reservationRepository.existsByTrip_TripIdAndPassengerId(tripId, passengerId);
    }

    @Override
    @Transactional
    public void confirmReservation(Long reservationId, String paymentIntentId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation with ID: "+reservationId+ " not found"));

        if(reservation.getStatus().equals(StatusReservation.CONFIRMED)){
            return;
        }

        if(!reservation.getStatus().equals(StatusReservation.PENDING)){
            throw new ReservationStatusNotAllowedToChangeException("Reservation with ID: " + reservationId +" is not PENDING. Curent status: "+reservation.getStatus());
        }

        reservation.setStatus(StatusReservation.CONFIRMED);
        reservationRepository.save(reservation);

        publishReservationConfirmedEvent(reservation);

    }

    @Override
    @Transactional
    public void cancelReservation(Long reservationId, String reason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation with ID: "+reservationId+ " not found"));

        if(reservation.getStatus().equals(StatusReservation.CANCELLED)){
            return;
        }

        Trip trip = reservation.getTrip();
        trip.restoreSeat();

        reservation.setStatus(StatusReservation.CANCELLED);
        reservationRepository.save(reservation);
        tripRepository.save(trip);

        publishReservationCancelledEvent(reservation, reason);

    }

    @Override
    public boolean checkAvailability(Long tripId) {
        return false;
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

    private void publishReservationCancelledEvent(Reservation reservation, String reasonP){
        ReservationCancelledEvent event = ReservationCancelledEvent.builder()
                .reservationId(reservation.getReservationId())
                .reason(reasonP)
                .timestamp(LocalDateTime.now())
                .build();

        notificationEventPublisher.publishReservationCancelled(event);
    }

    private void publishReservationConfirmedEvent(Reservation reservation){
        ReservationConfirmedEvent event = ReservationConfirmedEvent.builder()
                        .reservationId(reservation.getReservationId())
                                .timestamp(LocalDateTime.now())
                                        .build();

        notificationEventPublisher.publishReservationConfirmed(event);
    }
}
