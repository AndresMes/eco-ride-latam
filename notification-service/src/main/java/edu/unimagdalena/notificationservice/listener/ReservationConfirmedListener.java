package edu.unimagdalena.notificationservice.listener;


import edu.unimagdalena.notificationservice.dto.ReservationConfirmedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReservationConfirmedListener {

    @RabbitListener(queues = "${ecoride.rabbitmq.queues.notification-reservationConfirmed}")
    public void handleReservationConfirmed(String message) {
        log.info("✅ ReservationConfirmed event received: {}", message);

        log.info("📨 Sending reservation confirmation notification (mock)...");
    }

    @RabbitListener(queues = "${ecoride.rabbitmq.queues.notification-reservationConfirmed}")
    public void handleReservationConfirmed(ReservationConfirmedEvent event) {
        log.info("📌 [ReservationConfirmed] reservationId={} traceId={}",
                event.getReservationId(),
                org.slf4j.MDC.get("traceId"));

        log.info("📨 Sending reservation confirmation notification (mock)...");
    }
    /*
    @RabbitListener(queues = "${ecoride.rabbitmq.queues.notification-reservationConfirmed}")
    public void handleReservationConfirmed(ReservationConfirmedEvent event) {
        log.info("✅ Reservation confirmed for ID: {}", event.getReservationId());

        log.info("📨 Sending reservation confirmation notification (mock)...");
    }

    */
}
