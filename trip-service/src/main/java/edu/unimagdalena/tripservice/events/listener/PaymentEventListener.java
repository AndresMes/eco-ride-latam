package edu.unimagdalena.tripservice.events.listener;

import edu.unimagdalena.tripservice.events.PaymentAuthorizedEvent;
import edu.unimagdalena.tripservice.events.PaymentFailedEvent;
import edu.unimagdalena.tripservice.exceptions.PaymentException;
import edu.unimagdalena.tripservice.services.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final ReservationService reservationService;

    @RabbitListener(queues = "${ecoride.rabbitmq.queues.payment-authorized}")
    public void handlePaymentAuthorized(PaymentAuthorizedEvent event){
        log.info("Received PaymentAuthorizedEvent for reservationId={}", event.getReservationId());

        reservationService.confirmReservation(event.getReservationId(), event.getPaymentIntentId())
                .doOnSuccess(unused -> log.info("Reservation {} confirmed successfully", event.getReservationId()))
                .doOnError(err -> log.error("Failed to confirm reservation {}: {}", event.getReservationId(), err.getMessage(), err))
                // Suscribirse para que el flujo se ejecute:
                .subscribe();
    }

    @RabbitListener(queues = "${ecoride.rabbitmq.queues.payment-failed}")
    public void handlePaymentFailed(PaymentFailedEvent event){
        log.info("Received PaymentFailedEvent for reservationId={}", event.getReservationId());

        reservationService.cancelReservation(event.getReservationId(), event.getReason())
                .doOnSuccess(unused -> log.info("Reservation {} cancelled successfully", event.getReservationId()))
                .doOnError(err -> log.error("Failed to cancel reservation {}: {}", event.getReservationId(), err.getMessage(), err))
                .subscribe();
    }
}
