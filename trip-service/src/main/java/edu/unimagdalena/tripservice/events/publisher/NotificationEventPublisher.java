package edu.unimagdalena.tripservice.events.publisher;

import edu.unimagdalena.tripservice.events.ReservationCancelledEvent;
import edu.unimagdalena.tripservice.events.ReservationConfirmedEvent;
import edu.unimagdalena.tripservice.events.TripCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class NotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${ecoride.rabbitmq.exchanges.notification}")
    private String notificationExchange;

    @Value("${ecoride.rabbitmq.routing-keys.notification-reservationConfirmed}")
    private String reservationConfirmedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.notification-reservationCancelled}")
    private String reservationCancelledRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.trip-completed}")
    private String tripCompletedRoutingKey;

    public NotificationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishReservationConfirmed(ReservationConfirmedEvent event){
        try{

            event.setEventId(UUID.randomUUID().toString());

            rabbitTemplate.convertAndSend(
                    notificationExchange,
                    reservationConfirmedRoutingKey,
                    event
            );
        } catch (Exception e) {
            throw new RuntimeException("Falied to publish reservationConfirmed event", e);
        }
    }

    public void publishReservationCancelled(ReservationCancelledEvent event){
        try{

            event.setEventId(UUID.randomUUID().toString());

            rabbitTemplate.convertAndSend(
                    notificationExchange,
                    reservationCancelledRoutingKey,
                    event
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish reservationCancelled event", e);
        }
    }

    public void publishTripCompleted(TripCompletedEvent event){
        try{
            event.setEventId(UUID.randomUUID().toString());

            rabbitTemplate.convertAndSend(
                    notificationExchange,
                    tripCompletedRoutingKey,
                    event
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish TripCompleted event", e);
        }
    }
}
