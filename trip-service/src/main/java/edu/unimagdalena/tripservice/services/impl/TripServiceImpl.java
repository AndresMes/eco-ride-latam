package edu.unimagdalena.tripservice.services.impl;

import edu.unimagdalena.tripservice.dtos.TripDtoRequest;
import edu.unimagdalena.tripservice.dtos.TripDtoResponse;
import edu.unimagdalena.tripservice.dtos.TripDtoUpdateStatus;
import edu.unimagdalena.tripservice.entities.Trip;
import edu.unimagdalena.tripservice.enums.StatusTrip;
import edu.unimagdalena.tripservice.exceptions.notFound.TripNotFoundException;
import edu.unimagdalena.tripservice.mappers.TripMapper;
import edu.unimagdalena.tripservice.repositories.TripRepository;
import edu.unimagdalena.tripservice.services.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    TripRepository tripRepository;
    TripMapper tripMapper;

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
    public TripDtoResponse createTrip(TripDtoRequest dtoRequest) {
        Trip trip = tripMapper.toEntity(dtoRequest);
        trip.setStatus(StatusTrip.SCHEDULED);

        Trip saved =  tripRepository.save(trip);

        return tripMapper.toDtoResponse(saved);
    }

    @Override
    public TripDtoResponse updateTrip(Long id, TripDtoRequest dtoRequest) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException("Trip with ID: " + id + " not found"));

        tripMapper.updateTripFromDto(dtoRequest, trip);

        return tripMapper.toDtoResponse(tripRepository.save(trip));
    }

    @Override
    public TripDtoResponse updateTripStatus(Long id, TripDtoUpdateStatus newStatus) {

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new TripNotFoundException("Trip with ID: "+ id + " not found"));

        trip.setStatus(newStatus.newStatus());

        return tripMapper.toDtoResponse(tripRepository.save(trip));
    }
}
