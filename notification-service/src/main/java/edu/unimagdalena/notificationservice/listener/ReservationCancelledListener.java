package edu.unimagdalena.notificationservice.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReservationCancelledListener {

    @RabbitListener(queues = "${ecoride.rabbitmq.queues.notification-reservationCancelled}")
    public void handleReservationCancelled(String message) {
        log.info("✅ ReservationCancelled event received: {}", message);

        log.info("📨 Sending reservation cancellation notification (mock)...");
    }
}
