package edu.unimagdalena.notificationservice.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TripCompletedListener {

    @RabbitListener(queues = "${ecoride.rabbitmq.queues.trip-completed}")
    public void handleTripCompleted(String message) {
        log.info("✅ TripCompleted event received: {}", message);

        // Aquí más adelante puedes:
        // - Enviar notificación al pasajero
        // - Consultar PassengerService
        // - Guardar en outbox (si fuera opcional)

        // Por ahora solo lo mínimo:
        log.info("📨 Sending trip completion notification (mock)...");
    }
}
