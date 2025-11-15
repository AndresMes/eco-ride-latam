package edu.unimagdalena.tripservice.controllers;

import edu.unimagdalena.tripservice.dtos.TripDtoRequest;
import edu.unimagdalena.tripservice.dtos.TripDtoResponse;
import edu.unimagdalena.tripservice.dtos.TripDtoUpdateStatus;
import edu.unimagdalena.tripservice.services.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trips")
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

    @PutMapping("/{id}")
    public ResponseEntity<TripDtoResponse> updateTrip(@PathVariable Long id, @Valid @RequestBody TripDtoRequest dtoRequest){
        return ResponseEntity.status(HttpStatus.OK).body(tripService.updateTrip(id,dtoRequest));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TripDtoResponse> updateTripStatus(@PathVariable Long id, @Valid @RequestBody TripDtoUpdateStatus dtoUpdateStatus){
        return ResponseEntity.status(HttpStatus.OK).body(tripService.updateTripStatus(id, dtoUpdateStatus));
    }
}
