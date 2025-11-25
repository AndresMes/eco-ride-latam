package edu.unimagdalena.passengerservice.repositories;

import edu.unimagdalena.passengerservice.entities.Rating;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RatingRepository extends R2dbcRepository<Rating, Long> {

    @Query("SELECT * FROM rating WHERE to_id = :toId")
    Flux<Rating> findByToId(Long toId);

    Flux<Rating> findByTripId(Long tripId);

    @Query("SELECT AVG(score) FROM rating WHERE to_id = :driverId")
    Mono<Double> calculateAverageRating(Long driverId);

    @Query("SELECT EXISTS(SELECT 1 FROM rating WHERE trip_id = :tripId AND from_id = :fromPassengerId)")
    Mono<Boolean> existsByTripIdAndFromId(Long tripId, Long fromPassengerId);
}
