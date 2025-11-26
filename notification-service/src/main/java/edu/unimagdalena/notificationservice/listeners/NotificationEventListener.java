package edu.unimagdalena.notificationservice.listeners;

import edu.unimagdalena.notificationservice.events.PaymentAuthorizedEvent;
import edu.unimagdalena.notificationservice.events.PaymentFailedEvent;
import edu.unimagdalena.notificationservice.events.ReservationCancelledEvent;
import edu.unimagdalena.notificationservice.events.ReservationConfirmedEvent;
import edu.unimagdalena.notificationservice.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final EmailService emailService;

    // Email de prueba (CAMBIAR por obtención real del email del pasajero)
    private static final String TEST_EMAIL = "federicoescobar2022@gmail.com";

    @RabbitListener(queues = "${ecoride.rabbitmq.queues.notification-reservationConfirmed}")
    public void handleReservationConfirmed(ReservationConfirmedEvent event) {
        log.info("📬 Received ReservationConfirmedEvent: {}", event);

        try {
            // TODO: Obtener email real del pasajero desde PassengerService
            emailService.sendReservationConfirmedEmail(
                    TEST_EMAIL,
                    event.getReservationId()
            );

            log.info("✅ Notification sent for reservation confirmed: {}", event.getReservationId());
        } catch (Exception e) {
            log.error("❌ Failed to send notification for reservation confirmed: {}", event.getReservationId(), e);
            // Aquí podrías implementar lógica de retry o DLQ
        }
    }

    @RabbitListener(queues = "${ecoride.rabbitmq.queues.notification-reservationCancelled}")
    public void handleReservationCancelled(ReservationCancelledEvent event) {
        log.info("📬 Received ReservationCancelledEvent: {}", event);

        try {
            // TODO: Obtener email real del pasajero desde PassengerService
            emailService.sendReservationCancelledEmail(
                    TEST_EMAIL,
                    event.getReservationId(),
                    event.getReason()
            );

            log.info("✅ Notification sent for reservation cancelled: {}", event.getReservationId());
        } catch (Exception e) {
            log.error("❌ Failed to send notification for reservation cancelled: {}", event.getReservationId(), e);
        }
    }

    @RabbitListener(queues = "${ecoride.rabbitmq.queues.notification-paymentAuthorized}")
    public void handlePaymentAuthorized(PaymentAuthorizedEvent event) {
        log.info("📬 Received PaymentAuthorizedEvent: {}", event);

        try {
            // TODO: Obtener email real del pasajero desde PassengerService
            emailService.sendPaymentAuthorizedEmail(
                    TEST_EMAIL,
                    event.getReservationId(),
                    event.getPaymentIntentId()
            );

            log.info("✅ Notification sent for payment authorized: {}", event.getReservationId());
        } catch (Exception e) {
            log.error("❌ Failed to send notification for payment authorized: {}", event.getReservationId(), e);
        }
    }

    @RabbitListener(queues = "${ecoride.rabbitmq.queues.notification-paymentFailed}")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("📬 Received PaymentFailedEvent: {}", event);

        try {
            // TODO: Obtener email real del pasajero desde PassengerService
            emailService.sendPaymentFailedEmail(
                    TEST_EMAIL,
                    event.getReservationId(),
                    event.getReason()
            );

            log.info("✅ Notification sent for payment failed: {}", event.getReservationId());
        } catch (Exception e) {
            log.error("❌ Failed to send notification for payment failed: {}", event.getReservationId(), e);
        }
    }
}