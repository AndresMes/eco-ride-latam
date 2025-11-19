package edu.unimagdalena.tripservice.services.impl;

import edu.unimagdalena.tripservice.dtos.requests.ReservationDtoRequest;
import edu.unimagdalena.tripservice.dtos.requests.TripDtoRequest;
import edu.unimagdalena.tripservice.dtos.responses.ReservationCreatedDtoResponse;
import edu.unimagdalena.tripservice.dtos.responses.TripDtoResponse;
import edu.unimagdalena.tripservice.dtos.requests.TripDtoUpdateStatus;
import edu.unimagdalena.tripservice.entities.Trip;
import edu.unimagdalena.tripservice.enums.StatusTrip;
import edu.unimagdalena.tripservice.exceptions.ReservationAlreadyExistsException;
import edu.unimagdalena.tripservice.exceptions.TripStatusUpdateNotAllowedException;
import edu.unimagdalena.tripservice.exceptions.notFound.TripNotFoundException;
import edu.unimagdalena.tripservice.mappers.TripMapper;
import edu.unimagdalena.tripservice.repositories.ReservationRepository;
import edu.unimagdalena.tripservice.repositories.TripRepository;
import edu.unimagdalena.tripservice.services.ReservationService;
import edu.unimagdalena.tripservice.services.TripService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    @Override
    public TripDtoResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException("Trip with ID: " + id + " not found"));

        return tripMapper.toDtoResponse(trip);
    }

    @Override
    public List<TripDtoResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(tripMapper::toDtoResponse)
                .toList();
    }

    @Override
    public List<TripDtoResponse> searchTrips(Optional<String> origin,
                                             Optional<String> destination,
                                             Optional<LocalDateTime> from,
                                             Optional<LocalDateTime> to) {

        List<Trip> trips = tripRepository.searchTrips(
                origin.orElse(null),
                destination.orElse(null),
                from.orElse(null),
                to.orElse(null)
        );

        return trips.stream()
                .map(tripMapper::toDtoResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public TripDtoResponse createTrip(TripDtoRequest dtoRequest) {
        Trip trip = tripMapper.toEntity(dtoRequest);
        trip.setStatus(StatusTrip.SCHEDULED);

        Trip saved =  tripRepository.save(trip);

        return tripMapper.toDtoResponse(saved);
    }

    @Override
    @Transactional
    public TripDtoResponse updateTrip(Long id, TripDtoRequest dtoRequest) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException("Trip with ID: " + id + " not found"));

        tripMapper.updateTripFromDto(dtoRequest, trip);

        return tripMapper.toDtoResponse(tripRepository.save(trip));
    }

    @Override
    @Transactional
    public TripDtoResponse updateTripStatus(Long id, TripDtoUpdateStatus newStatus) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException("Trip with ID: "+ id + " not found"));

        if(trip.getStatus().equals(StatusTrip.CANCELLED) || trip.getStatus().equals(StatusTrip.FINISHED)){
            throw new TripStatusUpdateNotAllowedException(
                    "Cannot update status of a trip that is already " + trip.getStatus()
            );
        }
        trip.setStatus(newStatus.newStatus());

        return tripMapper.toDtoResponse(tripRepository.save(trip));
    }

    @Override
    @Transactional
    public ReservationCreatedDtoResponse createReservationInTrip(Long tripId, ReservationDtoRequest reservationDtoRequest) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip with ID: " + tripId + " not found"));

        trip.reserveSeat();

        if (reservationRepository.existsByTrip_TripIdAndPassengerId(tripId, reservationDtoRequest.passengerId())) {
            throw new ReservationAlreadyExistsException("Passenger already reserved this trip");
        }

        return reservationService.createReservation(tripId, reservationDtoRequest);


    }
}
