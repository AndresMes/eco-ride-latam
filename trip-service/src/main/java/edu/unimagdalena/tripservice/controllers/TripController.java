package edu.unimagdalena.tripservice.controllers;

import edu.unimagdalena.tripservice.dtos.requests.ReservationDtoRequest;
import edu.unimagdalena.tripservice.dtos.requests.TripDtoRequest;
import edu.unimagdalena.tripservice.dtos.responses.ReservationCreatedDtoResponse;
import edu.unimagdalena.tripservice.dtos.responses.TripDtoResponse;
import edu.unimagdalena.tripservice.dtos.requests.TripDtoUpdateStatus;
import edu.unimagdalena.tripservice.services.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripService tripService;

    @GetMapping("/all")
    public ResponseEntity<List<TripDtoResponse>> getAllTrips() {
        return ResponseEntity.ok(tripService.getAllTrips());
    }

    @GetMapping
    public ResponseEntity<List<TripDtoResponse>> searchTrips(
            @RequestParam Optional<String> origin,
            @RequestParam Optional<String> destination,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> to
    ) {
        return ResponseEntity.ok(
                tripService.searchTrips(origin, destination, from, to)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripDtoResponse> getTripById(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }

    @PostMapping
    public ResponseEntity<TripDtoResponse> createTrip(@Valid @RequestBody TripDtoRequest dtoRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createTrip(dtoRequest));
    }

    @PostMapping("/{tripId}/reservations")
    public ResponseEntity<ReservationCreatedDtoResponse> createReservationInTrip(@PathVariable Long tripId, @Valid @RequestBody ReservationDtoRequest dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createReservationInTrip(tripId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripDtoResponse> updateTrip(@PathVariable Long id, @Valid @RequestBody TripDtoRequest dtoRequest){
        return ResponseEntity.status(HttpStatus.OK).body(tripService.updateTrip(id,dtoRequest));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TripDtoResponse> updateTripStatus(@PathVariable Long id, @Valid @RequestBody TripDtoUpdateStatus dtoUpdateStatus){
        return ResponseEntity.status(HttpStatus.OK).body(tripService.updateTripStatus(id, dtoUpdateStatus));
    }
}
