package edu.unimagdalena.tripservice.controllers;

import edu.unimagdalena.tripservice.dtos.requests.ReservationDtoRequest;
import edu.unimagdalena.tripservice.dtos.requests.TripDtoRequest;
import edu.unimagdalena.tripservice.dtos.requests.TripDtoUpdateStatus;
import edu.unimagdalena.tripservice.dtos.responses.ReservationCreatedDtoResponse;
import edu.unimagdalena.tripservice.dtos.responses.TripDtoResponse;
import edu.unimagdalena.tripservice.services.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripService tripService;

    @GetMapping("/all")
    public Flux<TripDtoResponse> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping
    public Flux<TripDtoResponse> searchTrips(
            @RequestParam Optional<String> origin,
            @RequestParam Optional<String> destination,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Optional<LocalDateTime> to
    ) {
        return tripService.searchTrips(
                origin.orElse(null),
                destination.orElse(null),
                from.orElse(null),
                to.orElse(null)
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TripDtoResponse>> getTripById(@PathVariable Long id) {
        return tripService.getTripById(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<TripDtoResponse>> createTrip(@Valid @RequestBody TripDtoRequest dtoRequest,
                                                            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader){
        return tripService.createTrip(dtoRequest, authorizationHeader)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
    }

    @PostMapping("/{tripId}/reservations")
    public Mono<ResponseEntity<ReservationCreatedDtoResponse>> createReservationInTrip(
            @PathVariable Long tripId,
            @Valid @RequestBody ReservationDtoRequest dto,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {

        return tripService.createReservationInTrip(tripId, dto, authorizationHeader)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TripDtoResponse>> updateTrip(@PathVariable Long id, @Valid @RequestBody TripDtoRequest dtoRequest){
        return tripService.updateTrip(id, dtoRequest)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<TripDtoResponse>> updateTripStatus(@PathVariable Long id, @Valid @RequestBody TripDtoUpdateStatus dtoUpdateStatus){
        return tripService.updateTripStatus(id, dtoUpdateStatus)
                .map(ResponseEntity::ok);
    }
}
