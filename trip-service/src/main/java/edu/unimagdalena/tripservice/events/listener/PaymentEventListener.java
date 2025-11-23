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
        try{
            reservationService.confirmReservation(
                    event.getReservationId(),
                    event.getPaymentIntentId()
            );
        } catch (Exception e) {
            throw new PaymentException("Error processing PaymentAuthorized for reservation: "+event.getReservationId());
        }
    }

    @RabbitListener(queues = "${ecoride.rabbitmq.queues.payment-failed}")
    public void handlePaymentFailed(PaymentFailedEvent event){
        try{
            reservationService.cancelReservation(
                    event.getReservationId(),
                    event.getReason()
            );
        } catch (Exception e) {
            throw new PaymentException("Error processing PaymentFailed for reservation: " + event.getEventId());
        }
    }
}
