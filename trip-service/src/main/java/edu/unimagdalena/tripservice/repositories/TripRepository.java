package edu.unimagdalena.tripservice.repositories;

import edu.unimagdalena.tripservice.entities.Trip;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface TripRepository extends ReactiveCrudRepository<Trip, Long> {

    @Query("""
    SELECT *
    FROM trips t
    WHERE (:origin IS NULL OR t.origin ILIKE CONCAT('%', :origin, '%'))
      AND (:destination IS NULL OR t.destination ILIKE CONCAT('%', :destination, '%'))
      AND (:from IS NULL OR t.start_time >= :from)
      AND (:to IS NULL OR t.start_time <= :to)
""")
    Flux<Trip> searchTrips(String origin,
                           String destination,
                           LocalDateTime from,
                           LocalDateTime to);
}
