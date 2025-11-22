package edu.unimagdalena.tripservice.services.impl;

import edu.unimagdalena.tripservice.dtos.requests.ReservationDtoRequest;
import edu.unimagdalena.tripservice.dtos.responses.ReservationCreatedDtoResponse;
import edu.unimagdalena.tripservice.dtos.responses.ReservationDtoResponse;
import edu.unimagdalena.tripservice.entities.Reservation;
import edu.unimagdalena.tripservice.entities.Trip;
import edu.unimagdalena.tripservice.enums.StatusReservation;
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

        return reservationMapper.toCreatedDtoResponse(reservationRepository.save(reservation));

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
    public void confirmReservation(Long reservationId, String paymentIntentId) {

    }

    @Override
    public void cancelReservation(Long reservationId, String reason) {

    }

    @Override
    public boolean checkAvailability(Long tripId) {
        return false;
    }
}
