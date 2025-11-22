package edu.unimagdalena.passengerservice.repositories;

import edu.unimagdalena.passengerservice.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByKeycloakSub(String keycloakSub);
}
