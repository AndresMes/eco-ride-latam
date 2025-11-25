package edu.unimagdalena.tripservice.events.publisher;

import edu.unimagdalena.tripservice.events.TripCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TripEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${ecoride.rabbitmq.exchanges.trip}")
    private String tripExchange;

    @Value("${ecoride.rabbitmq.routing-keys.trip-completed}")
    private String tripCompletedRoutingKey;

    public void publishTripCompleted(TripCompletedEvent event){
        try{

            event.setEventId(UUID.randomUUID().toString());

            rabbitTemplate.convertAndSend(
                    tripExchange,
                    tripCompletedRoutingKey,
                    event
            );
        }catch (Exception ex){
            throw new RuntimeException("Failed to publish trip event completed event", ex);
        }
    }
}
