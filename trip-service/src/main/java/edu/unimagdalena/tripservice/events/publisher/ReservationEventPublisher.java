package edu.unimagdalena.tripservice.events.publisher;

import edu.unimagdalena.tripservice.events.ReservationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${ecoride.rabbitmq.exchanges.reservation}")
    private String reservationExchange;

    @Value("${ecoride.rabbitmq.routing-keys.reservation-requested}")
    private String reservationRequestedRoutingKey;


    public void publishReservationRequested(ReservationRequestedEvent event){
        try{
            event.setEventId(UUID.randomUUID().toString());

            rabbitTemplate.convertAndSend(
                    reservationExchange,
                    reservationRequestedRoutingKey,
                    event
            );
        }catch (Exception ex){
            throw new RuntimeException("Failed to publish reservation event", ex);
        }
    }
}
