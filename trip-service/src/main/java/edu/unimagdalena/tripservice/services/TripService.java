package edu.unimagdalena.tripservice.services;

import edu.unimagdalena.tripservice.dtos.requests.ReservationDtoRequest;
import edu.unimagdalena.tripservice.dtos.requests.TripDtoRequest;
import edu.unimagdalena.tripservice.dtos.responses.ReservationCreatedDtoResponse;
import edu.unimagdalena.tripservice.dtos.responses.TripDtoResponse;
import edu.unimagdalena.tripservice.dtos.requests.TripDtoUpdateStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TripService {

    TripDtoResponse getTripById(Long id);
    List<TripDtoResponse> getAllTrips();
    List<TripDtoResponse> searchTrips(Optional<String> origin,
                                      Optional<String> destination,
                                      Optional<LocalDateTime> from,
                                      Optional<LocalDateTime> to);
    TripDtoResponse createTrip(TripDtoRequest dtoRequest);
    TripDtoResponse updateTrip(Long id, TripDtoRequest dtoRequest);
    TripDtoResponse updateTripStatus(Long id, TripDtoUpdateStatus newStatus);
    ReservationCreatedDtoResponse createReservationInTrip(Long tripId, ReservationDtoRequest reservationDtoRequest);
}
