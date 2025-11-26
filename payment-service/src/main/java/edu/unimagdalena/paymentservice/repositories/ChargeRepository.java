package edu.unimagdalena.paymentservice.repositories;

import edu.unimagdalena.paymentservice.entities.Charge;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ChargeRepository extends ReactiveCrudRepository<Charge, Long> {
    Flux<Charge> findByPaymentIntentId(Long paymentIntentId);
}
