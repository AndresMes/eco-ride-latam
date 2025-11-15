package edu.unimagdalena.tripservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.unimagdalena.tripservice.entities.Trip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query("""
        SELECT t FROM Trip t
        WHERE 
            (:origin IS NULL OR LOWER(t.origin) = LOWER(:origin))
        AND (:destination IS NULL OR LOWER(t.destination) = LOWER(:destination))
        AND (:from IS NULL OR t.startTime >= :from)
        AND (:to IS NULL OR t.startTime <= :to)
    """)
    List<Trip> searchTrips(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
