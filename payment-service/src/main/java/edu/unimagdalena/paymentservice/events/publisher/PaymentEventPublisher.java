package edu.unimagdalena.paymentservice.events.publisher;


import edu.unimagdalena.paymentservice.events.PaymentAuthorizedEvent;
import edu.unimagdalena.paymentservice.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${ecoride.rabbitmq.exchanges.payment}")
    private String paymentExchange;

    @Value("${ecoride.rabbitmq.routing-keys.payment-failed}")
    private String paymentFailedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.payment-authorized}")
    private String paymentAuthorizedRoutingKey;


    public Mono<Void> publishPaymentAuthorized(PaymentAuthorizedEvent event) {
        if (event.getEventId() == null) event.setEventId(UUID.randomUUID().toString());
        return Mono.fromRunnable(() -> {
                    rabbitTemplate.convertAndSend(paymentExchange, paymentAuthorizedRoutingKey, event);

                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<Void> publishPaymentFailed(PaymentFailedEvent event) {
        if (event.getEventId() == null) event.setEventId(UUID.randomUUID().toString());
        return Mono.fromRunnable(() -> {
                    rabbitTemplate.convertAndSend(paymentExchange, paymentFailedRoutingKey, event);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }


}
