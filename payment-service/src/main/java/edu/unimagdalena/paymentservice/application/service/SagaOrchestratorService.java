package edu.unimagdalena.paymentservice.application.service;

import edu.unimagdalena.paymentservice.application.dto.request.CreatePaymentIntentRequest;
import edu.unimagdalena.paymentservice.application.dto.response.ChargeResponse;
import edu.unimagdalena.paymentservice.application.dto.response.PaymentIntentResponse;
import edu.unimagdalena.paymentservice.infrastructure.messaging.event.*;
import edu.unimagdalena.paymentservice.infrastructure.messaging.publisher.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Orquestador del patrón Saga para el proceso de reserva y pago
 *
 * FLUJO FELIZ (Happy Path):
 * 1. Recibe ReservationRequestedEvent de TripService
 * 2. Crea PaymentIntent
 * 3. Autoriza el pago
 * 4. Captura el pago
 * 5. Publica PaymentAuthorizedEvent (TripService confirma la reserva)
 *
 * FLUJO DE COMPENSACIÓN (Failure):
 * 1. Si el pago falla en cualquier paso
 * 2. Publica PaymentFailedEvent
 * 3. TripService cancela la reserva (compensación)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestratorService {

    private final PaymentService paymentService;
    private final PaymentEventPublisher eventPublisher;

    @Transactional
    public void processReservationPayment(ReservationRequestedEvent event) {
        String correlationId = event.getCorrelationId();
        Long reservationId = event.getReservationId();

        log.info("[SAGA-START] Processing payment for ReservationId: {}, CorrelationId: {}",
                reservationId, correlationId);

        try {
            // PASO 1: Crear PaymentIntent
            PaymentIntentResponse paymentIntent = createPaymentIntentFromEvent(event);
            log.info("[SAGA-STEP-1] PaymentIntent created - ID: {}, ReservationId: {}",
                    paymentIntent.getId(), reservationId);

            // PASO 2: Autorizar el pago
            paymentIntent = paymentService.authorizePayment(paymentIntent.getId());
            log.info("[SAGA-STEP-2] Payment authorized - PaymentIntentId: {}, ReservationId: {}",
                    paymentIntent.getId(), reservationId);

            // PASO 3: Capturar el pago
            ChargeResponse charge = paymentService.capturePayment(paymentIntent.getId());
            log.info("[SAGA-STEP-3] Payment captured - ChargeId: {}, PaymentIntentId: {}, ReservationId: {}",
                    charge.getId(), paymentIntent.getId(), reservationId);

            // PASO 4: Publicar evento de éxito
            publishPaymentAuthorized(event, paymentIntent, charge);
            log.info("[SAGA-SUCCESS] Payment process completed successfully - ReservationId: {}, CorrelationId: {}",
                    reservationId, correlationId);

            // PASO 5: Publicar evento para notificaciones
            publishReservationConfirmed(event);

        } catch (Exception e) {
            log.error("[SAGA-FAILED] Payment process failed - ReservationId: {}, CorrelationId: {}, Error: {}",
                    reservationId, correlationId, e.getMessage(), e);

            // COMPENSACIÓN: Publicar evento de fallo
            publishPaymentFailed(event, e.getMessage());

            // Publicar evento de cancelación para notificaciones
            publishReservationCancelled(event, e.getMessage());

            throw e;
        }
    }

    private PaymentIntentResponse createPaymentIntentFromEvent(ReservationRequestedEvent event) {
        CreatePaymentIntentRequest request = CreatePaymentIntentRequest.builder()
                .reservationId(event.getReservationId())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .description("Payment for trip " + event.getTripId() + " - Passenger " + event.getPassengerId())
                .idempotencyKey(generateIdempotencyKey(event))
                .correlationId(event.getCorrelationId())
                .build();

        return paymentService.createPaymentIntent(request);
    }

    private void publishPaymentAuthorized(
            ReservationRequestedEvent originalEvent,
            PaymentIntentResponse paymentIntent,
            ChargeResponse charge) {

        PaymentAuthorizedEvent event = PaymentAuthorizedEvent.builder()
                .reservationId(originalEvent.getReservationId())
                .paymentIntentId(paymentIntent.getId())
                .chargeId(charge.getId())
                .amount(charge.getAmount())
                .currency(charge.getCurrency())
                .correlationId(originalEvent.getCorrelationId())
                .timestamp(LocalDateTime.now())
                .authorizationCode(charge.getAuthorizationCode())
                .providerReference(charge.getProviderReference())
                .build();

        eventPublisher.publishPaymentAuthorized(event);
    }

    private void publishPaymentFailed(ReservationRequestedEvent originalEvent, String reason) {
        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .reservationId(originalEvent.getReservationId())
                .paymentIntentId(null) // Puede ser null si falló antes de crear el PaymentIntent
                .reason(reason)
                .correlationId(originalEvent.getCorrelationId())
                .timestamp(LocalDateTime.now())
                .errorCode("PAYMENT_FAILED")
                .errorDetails(reason)
                .build();

        eventPublisher.publishPaymentFailed(event);
    }

    private void publishReservationConfirmed(ReservationRequestedEvent originalEvent) {
        ReservationConfirmedEvent event = ReservationConfirmedEvent.builder()
                .reservationId(originalEvent.getReservationId())
                .tripId(originalEvent.getTripId())
                .passengerId(originalEvent.getPassengerId())
                .correlationId(originalEvent.getCorrelationId())
                .timestamp(LocalDateTime.now())
                .passengerEmail(originalEvent.getPassengerEmail())
                .passengerName("Passenger " + originalEvent.getPassengerId()) // TODO: Get from event
                .tripOrigin(originalEvent.getTripDescription())
                .tripDestination(originalEvent.getTripDescription())
                .tripStartTime(originalEvent.getTimestamp())
                .build();

        eventPublisher.publishReservationConfirmed(event);
    }

    private void publishReservationCancelled(ReservationRequestedEvent originalEvent, String reason) {
        ReservationCancelledEvent event = ReservationCancelledEvent.builder()
                .reservationId(originalEvent.getReservationId())
                .tripId(originalEvent.getTripId())
                .passengerId(originalEvent.getPassengerId())
                .reason(reason)
                .correlationId(originalEvent.getCorrelationId())
                .timestamp(LocalDateTime.now())
                .passengerEmail(originalEvent.getPassengerEmail())
                .passengerName("Passenger " + originalEvent.getPassengerId())
                .cancellationReason("Payment failed: " + reason)
                .build();

        eventPublisher.publishReservationCancelled(event);
    }

    private String generateIdempotencyKey(ReservationRequestedEvent event) {
        return "payment-" + event.getReservationId() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}