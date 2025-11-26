package edu.unimagdalena.paymentservice.services.impl;

import edu.unimagdalena.paymentservice.dtos.PaymentIntentResponse;
import edu.unimagdalena.paymentservice.dtos.responses.PaymentIntentResponse;
import edu.unimagdalena.paymentservice.entities.Charge;
import edu.unimagdalena.paymentservice.entities.PaymentIntent;
import edu.unimagdalena.paymentservice.enums.PaymentStatus;
import edu.unimagdalena.paymentservice.events.PaymentAuthorizedEvent;
import edu.unimagdalena.paymentservice.events.ReservationRequestedEvent;
import edu.unimagdalena.paymentservice.events.ReservationCancelledEvent;
import edu.unimagdalena.paymentservice.events.publisher.PaymentEventPublisher;
import edu.unimagdalena.paymentservice.mappers.PaymentIntentMapper;
import edu.unimagdalena.paymentservice.repositories.ChargeRepository;
import edu.unimagdalena.paymentservice.repositories.PaymentIntentRepository;
import edu.unimagdalena.paymentservice.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentIntentRepository paymentIntentRepository;
    private final ChargeRepository chargeRepository;
    private final PaymentIntentMapper paymentIntentMapper;
    private final PaymentEventPublisher eventPublisher;

    /**
     * Handler del evento ReservationRequested -> inicia SAGA (choreography)
     */
    @Override
    public Mono<edu.unimagdalena.paymentservice.dtos.responses.PaymentIntentResponse> processReservationRequested(ReservationRequestedEvent event) {
        log.info("Processing ReservationRequested event for reservationId: {}", event.getReservationId());

        PaymentIntent paymentIntent = paymentIntentMapper.ReservationRequestedEventToPaymentIntent(event);

        paymentIntent.setStatus(PaymentStatus.REQUIRES_ACTION);

        return authorizePaymentIntent(paymentIntent)
                .flatMap(authorizedIntent -> paymentIntendRepository.save(authorizedIntent))
                .flatMap(savedIntent -> createAndSaveCharge(savedIntent)
                        .map(charge -> Map.entry(savedIntent, charge)))
                .flatMap(entry -> {
                    PaymentIntent intent = entry.getKey();
                    Charge charge = entry.getValue();

                    return eventPublisher.publishPaymentAuthorized(
                            new PaymentAuthorizedEvent(
                                    intent.getReservationId(),
                                    intent.getId(),
                                    charge.getId(),
                                    "Conectarse con passenger service para solicitar email y nombre del pasajero",
                                    "Avendaño"
                                    // event.email(), // Asumiendo que viene en el evento
                                    // event.passengerName() // Asumiendo que viene en el evento
                            )
                    ).thenReturn(intent);
                })
                .map(paymentIntendMapper::paymentIntentToPaymentResponseDTO)
                .doOnSuccess(dto -> log.info("Successfully processed payment for reservationId: {}",
                        event.reservationId()))
                .doOnError(e -> log.error("Failed to process ReservationRequested for reservationId: {}",
                        event.reservationId(), e));
    }

    private Mono<PaymentIntent> authorizePaymentIntent(PaymentIntent paymentIntent) {
        log.info("Authorizing PaymentIntent for reservationId: {}", paymentIntent.getReservationId());

        // Aquí puedes agregar validaciones con OpenFeign (convertido a reactivo con WebClient)
        // Por ejemplo: return reservationClient.validateReservation(paymentIntent.getReservationId())
        //                  .thenReturn(paymentIntent);

        // Por ahora solo retorna el intent autorizado
        paymentIntent.setStatus(PaymentStatus.AUTHORIZED);
        return Mono.just(paymentIntent)
                .doOnNext(intent -> log.info("PaymentIntent authorized for reservationId: {}",
                        intent.getReservationId()));
    }

    private Mono<Charge> createAndSaveCharge(PaymentIntent paymentIntent) {
        Charge charge = Charge.builder()
                .id(UUID.randomUUID().toString())
                .paymentIntentId(paymentIntent.getId())
                .capturedAt(LocalDateTime.now())
                .build();

        return chargeRepository.save(charge)
                .doOnSuccess(savedCharge -> log.info("Charge created with id: {}", savedCharge.getId()));
    }

    @Override
    public Mono<Void> processReservationCancelled(ReservationCancelledEvent event) {
        log.info("Processing ReservationCancelled event for reservationId: {}", event.reservationId());

        return paymentIntendRepository.findByReservationId(event.reservationId())
                .switchIfEmpty(Mono.error(new PaymentNotFoundException(
                        "Payment not found for reservationId: " + event.reservationId())))
                .flatMap(paymentIntent -> {
                    paymentIntent.setStatus(PaymentStatus.FAILED);
                    return paymentIntendRepository.save(paymentIntent);
                })
                .doOnSuccess(paymentIntent -> log.info("Payment cancelled for reservationId: {}",
                        event.reservationId()))
                .doOnError(e -> log.error("Failed to cancel payment for reservationId: {}",
                        event.reservationId(), e))
                .then();
    }
}
