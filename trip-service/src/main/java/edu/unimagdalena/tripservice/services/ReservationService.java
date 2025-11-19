package edu.unimagdalena.tripservice.services;

import edu.unimagdalena.tripservice.dtos.requests.ReservationDtoRequest;
import edu.unimagdalena.tripservice.dtos.responses.ReservationCreatedDtoResponse;
import edu.unimagdalena.tripservice.dtos.responses.ReservationDtoResponse;

import java.util.List;

public interface ReservationService {

    ReservationCreatedDtoResponse createReservation(Long tripId, ReservationDtoRequest dtoRequest);
    ReservationDtoResponse getReservationById(Long reservationId);
    List<ReservationDtoResponse> getReservationsByPassenger(Long passengerId);
    List<ReservationDtoResponse> getReservationsByTrip(Long tripId);
    void confirmReservation(Long reservationId, String paymentIntentId);
    void cancelReservation(Long reservationId, String reason);
    boolean checkAvailability(Long tripId);
}
