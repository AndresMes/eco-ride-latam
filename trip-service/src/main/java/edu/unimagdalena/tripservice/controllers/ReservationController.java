package edu.unimagdalena.tripservice.controllers;

import edu.unimagdalena.tripservice.dtos.responses.ReservationDtoResponse;
import edu.unimagdalena.tripservice.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDtoResponse> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<ReservationDtoResponse>> getReservationsByPassanger(@PathVariable Long passengerId){
        return ResponseEntity.ok(reservationService.getReservationsByPassenger(passengerId));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ReservationDtoResponse>> getReservationsByTrip(@PathVariable Long tripId){
        return ResponseEntity.ok(reservationService.getReservationsByTrip(tripId));
    }

    @GetMapping("/exists-by-trip-and-passenger")
    public ResponseEntity<Boolean> existsByTripAndPassenger(@RequestParam Long tripId, @RequestParam Long passengerId){
        return ResponseEntity.ok(reservationService.existsByTripAndPassengerId(tripId, passengerId));
    }
}
