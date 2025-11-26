package edu.unimagdalena.paymentservice.services;

import edu.unimagdalena.paymentservice.dtos.responses.PaymentIntentResponse;
import edu.unimagdalena.paymentservice.events.ReservationCancelledEvent;
import edu.unimagdalena.paymentservice.events.ReservationRequestedEvent;
import reactor.core.publisher.Mono;

public interface PaymentService {

    Mono<PaymentIntentResponse> processReservationRequested(ReservationRequestedEvent event);

    Mono<Void> processReservationCancelled(ReservationCancelledEvent event);
}
