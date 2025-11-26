package edu.unimagdalena.paymentservice.events.listener;

import edu.unimagdalena.paymentservice.events.ReservationRequestedEvent;
import edu.unimagdalena.paymentservice.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final PaymentService paymentService;

    @RabbitListener(queues = "${ecoride.rabbitmq.queues.reservation-requested}")
    public void handleReservationRequested(ReservationRequestedEvent event) {

        try {

            paymentService.processPaymentForReservation(event)
                    .block();

        } catch (Exception e) {

            paymentService.publishPaymentFailed(
                    event.getReservationId(),
                    "Critical error: " + e.getMessage()
            ).subscribe();
        }
    }
}
