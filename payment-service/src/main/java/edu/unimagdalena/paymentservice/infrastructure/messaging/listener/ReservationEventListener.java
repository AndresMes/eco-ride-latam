package edu.unimagdalena.paymentservice.infrastructure.messaging.listener;

import edu.unimagdalena.paymentservice.application.service.SagaOrchestratorService;
import edu.unimagdalena.paymentservice.infrastructure.messaging.event.ReservationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationEventListener {

    private final SagaOrchestratorService sagaOrchestratorService;

    /**
     * Escucha eventos de ReservationRequested y inicia el proceso de pago (Saga)
     */
    @RabbitListener(queues = "${ecoride.rabbitmq.queues.reservation-requested}")
    public void handleReservationRequested(ReservationRequestedEvent event) {
        log.info("Received ReservationRequestedEvent - ReservationId: {}, Amount: {}, CorrelationId: {}",
                event.getReservationId(), event.getAmount(), event.getCorrelationId());

        try {
            // Validar el evento
            validateEvent(event);

            // Iniciar el proceso de pago a través del Saga Orchestrator
            sagaOrchestratorService.processReservationPayment(event);

            log.info("ReservationRequestedEvent processed successfully - ReservationId: {}",
                    event.getReservationId());

        } catch (IllegalArgumentException e) {
            log.error("Invalid ReservationRequestedEvent - ReservationId: {}, Error: {}",
                    event.getReservationId(), e.getMessage());
            // Aquí podrías publicar un evento de error o mover a DLQ
            throw e;

        } catch (Exception e) {
            log.error("Error processing ReservationRequestedEvent - ReservationId: {}, Error: {}",
                    event.getReservationId(), e.getMessage(), e);
            throw new RuntimeException("Failed to process ReservationRequestedEvent", e);
        }
    }

    /**
     * Valida que el evento tenga todos los datos necesarios
     */
    private void validateEvent(ReservationRequestedEvent event) {
        if (event.getReservationId() == null) {
            throw new IllegalArgumentException("ReservationId is required");
        }
        if (event.getAmount() == null || event.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (event.getCurrency() == null || event.getCurrency().isBlank()) {
            throw new IllegalArgumentException("Currency is required");
        }
        if (event.getCorrelationId() == null || event.getCorrelationId().isBlank()) {
            throw new IllegalArgumentException("CorrelationId is required");
        }
    }
}