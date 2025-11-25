package edu.unimagdalena.tripservice.controllers;

import edu.unimagdalena.tripservice.dtos.responses.ReservationDtoResponse;
import edu.unimagdalena.tripservice.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ReservationDtoResponse>> getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/passenger/{passengerId}")
    public Flux<ReservationDtoResponse> getReservationsByPassenger(@PathVariable Long passengerId){
        return reservationService.getReservationsByPassenger(passengerId);
    }

    @GetMapping("/trip/{tripId}")
    public Flux<ReservationDtoResponse> getReservationsByTrip(@PathVariable Long tripId){
        return reservationService.getReservationsByTrip(tripId);
    }

    @GetMapping("/exists-by-trip-and-passenger")
    public Mono<ResponseEntity<Boolean>> existsByTripAndPassenger(
            @RequestParam Long tripId,
            @RequestParam Long passengerId
    ){
        return reservationService.existsByTripAndPassengerId(tripId, passengerId)
                .map(ResponseEntity::ok);
    }
}
