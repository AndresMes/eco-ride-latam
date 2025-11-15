package edu.unimagdalena.tripservice.repositories;

import edu.unimagdalena.tripservice.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
