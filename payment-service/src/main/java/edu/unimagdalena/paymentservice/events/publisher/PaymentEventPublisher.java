package edu.unimagdalena.paymentservice.events.publisher;

import edu.unimagdalena.paymentservice.events.PaymentAuthorizedEvent;
import edu.unimagdalena.paymentservice.events.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);

    @Value("${ecoride.rabbitmq.exchanges.payment}")
    private String paymentExchange;

    @Value("${ecoride.rabbitmq.routing-keys.payment-authorized}")
    private String paymentAuthorizedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.payment-failed}")
    private String paymentFailedRoutingKey;

    // 🔔 Routing Keys para NotificationService
    @Value("${ecoride.rabbitmq.routing-keys.notification-payment-authorized}")
    private String notificationPaymentAuthorizedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.notification-payment-failed}")
    private String notificationPaymentFailedRoutingKey;

    public Mono<Void> publishPaymentAuthorized(PaymentAuthorizedEvent event) {
        return Mono.fromRunnable(() -> {
            try {
                event.setEventId(UUID.randomUUID().toString());
                event.setTimestamp(LocalDateTime.now());

                // 1️⃣ Publicar para TripService
                rabbitTemplate.convertAndSend(
                        paymentExchange,
                        paymentAuthorizedRoutingKey,
                        event
                );
                log.debug("Event sent to TripService with routing key: {}", paymentAuthorizedRoutingKey);

                // 2️⃣ Publicar para NotificationService
                rabbitTemplate.convertAndSend(
                        paymentExchange,
                        notificationPaymentAuthorizedRoutingKey,
                        event
                );
                log.debug("Event sent to NotificationService with routing key: {}", notificationPaymentAuthorizedRoutingKey);

                log.info("PaymentAuthorizedEvent published successfully to both services (reservationId: {})", event.getReservationId());

            } catch (Exception e) {
                log.error("Failed to publish PaymentAuthorizedEvent: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to publish PaymentAuthorizedEvent", e);
            }
        });
    }

    public Mono<Void> publishPaymentFailed(PaymentFailedEvent event) {
        return Mono.fromRunnable(() -> {
            try {
                event.setEventId(UUID.randomUUID().toString());
                event.setTimestamp(LocalDateTime.now());

                // 1️⃣ Publicar para TripService
                rabbitTemplate.convertAndSend(
                        paymentExchange,
                        paymentFailedRoutingKey,
                        event
                );
                log.debug("Event sent to TripService with routing key: {}", paymentFailedRoutingKey);

                // 2️⃣ Publicar para NotificationService
                rabbitTemplate.convertAndSend(
                        paymentExchange,
                        notificationPaymentFailedRoutingKey,
                        event
                );
                log.debug("Event sent to NotificationService with routing key: {}", notificationPaymentFailedRoutingKey);

                log.info("PaymentFailedEvent published successfully to both services (reservationId: {})", event.getReservationId());

            } catch (Exception e) {
                log.error("Failed to publish PaymentFailedEvent: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to publish PaymentFailedEvent", e);
            }
        });
    }
}