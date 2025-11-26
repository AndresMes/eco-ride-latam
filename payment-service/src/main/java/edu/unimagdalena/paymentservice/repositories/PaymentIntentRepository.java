package edu.unimagdalena.paymentservice.repositories;

import edu.unimagdalena.paymentservice.entities.PaymentIntent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface PaymentIntentRepository extends ReactiveCrudRepository<PaymentIntent, Long> {
    Mono<PaymentIntent> findByReservationId(Long reservationId);
}
