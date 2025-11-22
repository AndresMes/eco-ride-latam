package edu.unimagdalena.tripservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.unimagdalena.tripservice.entities.Trip;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    @Query(value = """
    SELECT *
    FROM trips t
    WHERE ( CAST(:origin AS text) IS NULL OR t.origin ILIKE CONCAT('%', CAST(:origin AS text), '%') )
      AND ( CAST(:destination AS text) IS NULL OR t.destination ILIKE CONCAT('%', CAST(:destination AS text), '%') )
      AND ( CAST(:from AS timestamp) IS NULL OR t.start_time >= CAST(:from AS timestamp) )
      AND ( CAST(:to AS timestamp) IS NULL OR t.start_time <= CAST(:to AS timestamp) )
    """, nativeQuery = true)
    List<Trip> searchTrips(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
