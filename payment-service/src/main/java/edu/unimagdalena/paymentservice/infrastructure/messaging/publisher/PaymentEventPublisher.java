package edu.unimagdalena.paymentservice.infrastructure.messaging.publisher;

import edu.unimagdalena.paymentservice.infrastructure.messaging.event.PaymentAuthorizedEvent;
import edu.unimagdalena.paymentservice.infrastructure.messaging.event.PaymentFailedEvent;
import edu.unimagdalena.paymentservice.infrastructure.messaging.event.ReservationConfirmedEvent;
import edu.unimagdalena.paymentservice.infrastructure.messaging.event.ReservationCancelledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${ecoride.rabbitmq.exchange}")
    private String exchange;

    @Value("${ecoride.rabbitmq.routing-keys.payment-authorized}")
    private String paymentAuthorizedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.payment-failed}")
    private String paymentFailedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.notification-reservationConfirmed}")
    private String notificationReservationConfirmedRoutingKey;

    @Value("${ecoride.rabbitmq.routing-keys.notification-reservationCancelled}")
    private String notificationReservationCancelledRoutingKey;

    /**
     * Publica evento de pago autorizado exitosamente
     */
    public void publishPaymentAuthorized(PaymentAuthorizedEvent event) {
        try {
            log.info("Publishing PaymentAuthorizedEvent - ReservationId: {}, CorrelationId: {}",
                    event.getReservationId(), event.getCorrelationId());

            rabbitTemplate.convertAndSend(
                    exchange,
                    paymentAuthorizedRoutingKey,
                    event
            );

            log.info("PaymentAuthorizedEvent published successfully - ReservationId: {}",
                    event.getReservationId());
        } catch (Exception e) {
            log.error("Error publishing PaymentAuthorizedEvent - ReservationId: {}, Error: {}",
                    event.getReservationId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish PaymentAuthorizedEvent", e);
        }
    }

    /**
     * Publica evento de pago fallido
     */
    public void publishPaymentFailed(PaymentFailedEvent event) {
        try {
            log.info("Publishing PaymentFailedEvent - ReservationId: {}, Reason: {}, CorrelationId: {}",
                    event.getReservationId(), event.getReason(), event.getCorrelationId());

            rabbitTemplate.convertAndSend(
                    exchange,
                    paymentFailedRoutingKey,
                    event
            );

            log.info("PaymentFailedEvent published successfully - ReservationId: {}",
                    event.getReservationId());
        } catch (Exception e) {
            log.error("Error publishing PaymentFailedEvent - ReservationId: {}, Error: {}",
                    event.getReservationId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish PaymentFailedEvent", e);
        }
    }

    /**
     * Publica evento de reserva confirmada (para NotificationService)
     */
    public void publishReservationConfirmed(ReservationConfirmedEvent event) {
        try {
            log.info("Publishing ReservationConfirmedEvent - ReservationId: {}, CorrelationId: {}",
                    event.getReservationId(), event.getCorrelationId());

            rabbitTemplate.convertAndSend(
                    exchange,
                    notificationReservationConfirmedRoutingKey,
                    event
            );

            log.info("ReservationConfirmedEvent published successfully - ReservationId: {}",
                    event.getReservationId());
        } catch (Exception e) {
            log.error("Error publishing ReservationConfirmedEvent - ReservationId: {}, Error: {}",
                    event.getReservationId(), e.getMessage(), e);
            // No lanzamos excepción aquí porque las notificaciones no son críticas
            log.warn("Failed to publish ReservationConfirmedEvent, but continuing...");
        }
    }

    /**
     * Publica evento de reserva cancelada (para NotificationService)
     */
    public void publishReservationCancelled(ReservationCancelledEvent event) {
        try {
            log.info("Publishing ReservationCancelledEvent - ReservationId: {}, CorrelationId: {}",
                    event.getReservationId(), event.getCorrelationId());

            rabbitTemplate.convertAndSend(
                    exchange,
                    notificationReservationCancelledRoutingKey,
                    event
            );

            log.info("ReservationCancelledEvent published successfully - ReservationId: {}",
                    event.getReservationId());
        } catch (Exception e) {
            log.error("Error publishing ReservationCancelledEvent - ReservationId: {}, Error: {}",
                    event.getReservationId(), e.getMessage(), e);
            // No lanzamos excepción aquí porque las notificaciones no son críticas
            log.warn("Failed to publish ReservationCancelledEvent, but continuing...");
        }
    }
}