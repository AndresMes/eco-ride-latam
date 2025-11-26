package edu.unimagdalena.paymentservice.events.publisher;

import edu.unimagdalena.paymentservice.events.PaymentAuthorizedEvent;
import edu.unimagdalena.paymentservice.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${ecoride.rabbitmq.exchanges.payment}")
    private String paymentExchange;

    @Value("${ecoride.rabbitmq.routing-keys.payment-authorized}")
    private String paymentAuthorizedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.payment-failed}")
    private String paymentFailedRoutingKey;

    public Mono<Void> publishPaymentAuthorized(PaymentAuthorizedEvent event) {
        return Mono.fromRunnable(() -> {
            try {
                event.setEventId(UUID.randomUUID().toString());
                event.setTimestamp(LocalDateTime.now());

                rabbitTemplate.convertAndSend(
                        paymentExchange,
                        paymentAuthorizedRoutingKey,
                        event
                );

            } catch (Exception e) {
                throw new RuntimeException("Failed to publish PaymentAuthorizedEvent", e);
            }
        });
    }

    public Mono<Void> publishPaymentFailed(PaymentFailedEvent event) {
        return Mono.fromRunnable(() -> {
            try {
                event.setEventId(UUID.randomUUID().toString());
                event.setTimestamp(LocalDateTime.now());

                rabbitTemplate.convertAndSend(
                        paymentExchange,
                        paymentFailedRoutingKey,
                        event
                );


            } catch (Exception e) {
                throw new RuntimeException("Failed to publish PaymentFailedEvent", e);
            }
        });
    }
}