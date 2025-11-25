package edu.unimagdalena.tripservice.repositories;

import edu.unimagdalena.tripservice.entities.Reservation;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReservationRepository extends ReactiveCrudRepository<Reservation, Long> {
    Flux<Reservation> findAllByPassengerId(Long passengerId);
    Flux<Reservation> findAllByTripId(Long tripId);
    Mono<Boolean> existsByTripIdAndPassengerId(Long tripId, Long passengerId);
}
