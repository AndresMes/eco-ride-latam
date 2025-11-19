package edu.unimagdalena.tripservice.repositories;

import edu.unimagdalena.tripservice.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByPassengerId(Long id);
    List<Reservation> findAllByTrip_TripId(Long id);
    boolean existsByTrip_TripIdAndPassengerId(Long tripId, Long passengerId);
}
