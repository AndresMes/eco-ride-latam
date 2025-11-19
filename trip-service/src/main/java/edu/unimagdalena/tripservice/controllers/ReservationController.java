package edu.unimagdalena.tripservice.controllers;

import edu.unimagdalena.tripservice.dtos.responses.ReservationDtoResponse;
import edu.unimagdalena.tripservice.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/passanger/{passengerId}")
    public ResponseEntity<List<ReservationDtoResponse>> getReservationsByPassanger(@PathVariable Long passengerId){
        return ResponseEntity.ok(reservationService.getReservationsByPassenger(passengerId));
    }

    @GetMapping("/trip({tripId}")
    public ResponseEntity<List<ReservationDtoResponse>> getReservationsByTrip(@PathVariable Long tripId){
        return ResponseEntity.ok(reservationService.getReservationsByTrip(tripId));
    }
}
