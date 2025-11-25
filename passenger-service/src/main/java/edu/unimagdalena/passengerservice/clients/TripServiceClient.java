package edu.unimagdalena.passengerservice.clients;

import edu.unimagdalena.passengerservice.dtos.trip.TripDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trip-service")
public interface TripServiceClient {

    @GetMapping("/api/v1/trips/{tripId}")
    TripDto getTripById(@PathVariable Long tripId);

    @GetMapping("/api/v1/reservations/exists-by-trip-and-passenger")
    Boolean existsByTripAndPassenger(@RequestParam Long tripId, @RequestParam Long passengerId);

}
