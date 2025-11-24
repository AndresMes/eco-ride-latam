package edu.unimagdalena.passengerservice.repositories;

import edu.unimagdalena.passengerservice.entities.Passenger;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface PassengerRepository extends R2dbcRepository<Passenger, Long> {

    Mono<Passenger> findByKeycloakSub(String keycloakSub);
}