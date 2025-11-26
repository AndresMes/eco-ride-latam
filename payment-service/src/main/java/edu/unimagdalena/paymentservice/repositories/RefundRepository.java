package edu.unimagdalena.paymentservice.repositories;

import edu.unimagdalena.paymentservice.entities.Refund;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RefundRepository extends ReactiveCrudRepository<Refund, Long> {
    Flux<Refund> findByChargeId(Long chargeId);
}
