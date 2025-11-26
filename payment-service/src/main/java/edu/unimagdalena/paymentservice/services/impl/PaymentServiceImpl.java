package edu.unimagdalena.paymentservice.services.impl;

import edu.unimagdalena.paymentservice.dtos.requests.PaymentIntentRequest;
import edu.unimagdalena.paymentservice.dtos.responses.ChargeResponse;
import edu.unimagdalena.paymentservice.dtos.responses.PaymentIntentResponse;
import edu.unimagdalena.paymentservice.dtos.responses.RefundResponse;
import edu.unimagdalena.paymentservice.entities.Charge;
import edu.unimagdalena.paymentservice.entities.PaymentIntent;
import edu.unimagdalena.paymentservice.entities.Refund;
import edu.unimagdalena.paymentservice.enums.PaymentProvider;
import edu.unimagdalena.paymentservice.enums.PaymentStatus;
import edu.unimagdalena.paymentservice.events.PaymentAuthorizedEvent;
import edu.unimagdalena.paymentservice.events.PaymentFailedEvent;
import edu.unimagdalena.paymentservice.events.ReservationRequestedEvent;
import edu.unimagdalena.paymentservice.events.publisher.PaymentEventPublisher;
import edu.unimagdalena.paymentservice.exceptions.PaymentAuthorizationException;
import edu.unimagdalena.paymentservice.exceptions.notFound.PaymentNotFoundException;
import edu.unimagdalena.paymentservice.repositories.ChargeRepository;
import edu.unimagdalena.paymentservice.repositories.PaymentIntentRepository;
import edu.unimagdalena.paymentservice.repositories.RefundRepository;
import edu.unimagdalena.paymentservice.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentIntentRepository paymentIntentRepository;
    private final ChargeRepository chargeRepository;
    private final RefundRepository refundRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    @Value("${payment.provider.mock:true}")
    private boolean useMockProvider;

    @Value("${payment.provider.timeout-seconds:5}")
    private int timeoutSeconds;

    private final Random random = new Random();


    @Override
    @Transactional
    public Mono<Void> processPaymentForReservation(ReservationRequestedEvent event) {

        return createPaymentIntentForReservation(event)
                .flatMap(paymentIntent ->
                        authorizeAndCapture(paymentIntent.paymentIntentId())
                                .flatMap(charge -> {
                                    PaymentAuthorizedEvent authorizedEvent = PaymentAuthorizedEvent.builder()
                                            .reservationId(event.getReservationId())
                                            .paymentIntentId(String.valueOf(paymentIntent.paymentIntentId()))
                                            .chargeId(String.valueOf(charge.chargeId()))
                                            .build();

                                    return paymentEventPublisher.publishPaymentAuthorized(authorizedEvent);
                                })
                )
                .onErrorResume(error -> {

                    return publishPaymentFailed(
                            event.getReservationId(),
                            error.getMessage()
                    );
                });
    }

    @Override
    public Mono<Void> publishPaymentFailed(Long reservationId, String reason) {
        PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                .reservationId(reservationId)
                .reason(reason)
                .build();

        return paymentEventPublisher.publishPaymentFailed(failedEvent);
    }


    @Override
    @Transactional
    public Mono<PaymentIntentResponse> createPaymentIntent(PaymentIntentRequest request) {

        PaymentIntent paymentIntent = PaymentIntent.builder()
                .reservationId(request.reservationId())
                .amount(request.amount())
                .currency("COP")
                .status(PaymentStatus.REQUIRES_ACTION)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return paymentIntentRepository.save(paymentIntent)
                .map(this::toPaymentIntentResponse);
    }

    @Override
    public Mono<PaymentIntentResponse> getPaymentIntent(Long paymentIntentId) {
        return paymentIntentRepository.findById(paymentIntentId)
                .map(this::toPaymentIntentResponse)
                .switchIfEmpty(Mono.error(
                        new PaymentNotFoundException("Payment intent not found: " + paymentIntentId)
                ));
    }

    @Override
    public Mono<PaymentIntentResponse> getPaymentIntentByReservation(Long reservationId) {
        return paymentIntentRepository.findByReservationId(reservationId)
                .map(this::toPaymentIntentResponse)
                .switchIfEmpty(Mono.error(
                        new PaymentNotFoundException("Payment intent not found for reservation: " + reservationId)
                ));
    }


    @Override
    @Transactional
    public Mono<ChargeResponse> authorizeAndCapture(Long paymentIntentId) {

        return paymentIntentRepository.findById(paymentIntentId)
                .switchIfEmpty(Mono.error(
                        new PaymentNotFoundException("Payment intent not found: " + paymentIntentId)
                ))
                .flatMap(paymentIntent -> {
                    return simulatePaymentProvider(paymentIntent)
                            .flatMap(providerResponse -> {
                                paymentIntent.setStatus(PaymentStatus.CAPTURED);
                                paymentIntent.setUpdatedAt(LocalDateTime.now());

                                return paymentIntentRepository.save(paymentIntent)
                                        .flatMap(updated -> {
                                            Charge charge = Charge.builder()
                                                    .paymentIntentId(updated.getPaymentIntentId())
                                                    .provider(PaymentProvider.MOCK_PROVIDER)
                                                    .providerRef(providerResponse)
                                                    .capturedAt(LocalDateTime.now())
                                                    .build();

                                            return chargeRepository.save(charge);
                                        });
                            })
                            .map(this::toChargeResponse);

                });
    }

    @Override
    public Mono<ChargeResponse> getCharge(Long chargeId) {
        return chargeRepository.findById(chargeId)
                .map(this::toChargeResponse)
                .switchIfEmpty(Mono.error(
                        new PaymentNotFoundException("Charge not found: " + chargeId)
                ));
    }

    @Override
    @Transactional
    public Mono<RefundResponse> refund(Long chargeId, String reason) {

        return chargeRepository.findById(chargeId)
                .switchIfEmpty(Mono.error(
                        new PaymentNotFoundException("Charge not found: " + chargeId)
                ))
                .flatMap(charge ->
                        paymentIntentRepository.findById(charge.getPaymentIntentId())
                                .flatMap(paymentIntent -> {
                                    return simulateRefundProvider(charge)
                                            .flatMap(refundRef -> {
                                                paymentIntent.setStatus(PaymentStatus.REFUNDED);
                                                paymentIntent.setUpdatedAt(LocalDateTime.now());

                                                return paymentIntentRepository.save(paymentIntent)
                                                        .flatMap(updated -> {
                                                            Refund refund = Refund.builder()
                                                                    .chargeId(chargeId)
                                                                    .amount(paymentIntent.getAmount())
                                                                    .reason(reason)
                                                                    .createdAt(LocalDateTime.now())
                                                                    .build();

                                                            return refundRepository.save(refund);
                                                        });
                                            });
                                })
                )
                .map(this::toRefundResponse);
    }

    @Override
    public Mono<RefundResponse> getRefund(Long refundId) {
        return refundRepository.findById(refundId)
                .map(this::toRefundResponse)
                .switchIfEmpty(Mono.error(
                        new PaymentNotFoundException("Refund not found: " + refundId)
                ));
    }


    private Mono<PaymentIntentResponse> createPaymentIntentForReservation(ReservationRequestedEvent event) {
        PaymentIntentRequest request = new PaymentIntentRequest(
                event.getReservationId(),
                event.getAmount()
        );
        return createPaymentIntent(request);
    }

    private Mono<String> simulatePaymentProvider(PaymentIntent paymentIntent) {
        if (!useMockProvider) {
            // Aquí iría la integración real con Stripe, PayPal, etc.
            return Mono.error(new UnsupportedOperationException("Real provider not implemented"));
        }

        return Mono.delay(Duration.ofSeconds(1))
                .flatMap(delay -> {
                    boolean success = random.nextInt(100) < 70; //random.nextInt(100) < 70 false

                    if (success) {
                        String providerRef = "MOCK_" + UUID.randomUUID().toString();
                        return Mono.just(providerRef);
                    } else {
                        return Mono.error(new PaymentAuthorizationException("Insufficient funds"));
                    }
                })
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .onErrorResume(error -> {
                    if (error instanceof PaymentAuthorizationException) {
                        return Mono.error(error);
                    }
                    return Mono.error(new PaymentAuthorizationException("Payment timeout or provider error"));
                });
    }


    private Mono<String> simulateRefundProvider(Charge charge) {
        if (!useMockProvider) {
            return Mono.error(new UnsupportedOperationException("Real provider not implemented"));
        }

        return Mono.delay(Duration.ofSeconds(1))
                .map(delay -> {
                    String refundRef = "REFUND_" + UUID.randomUUID().toString();
                    return refundRef;
                })
                .timeout(Duration.ofSeconds(timeoutSeconds));
    }

    // ============ Mappers ============

    private PaymentIntentResponse toPaymentIntentResponse(PaymentIntent entity) {
        return new PaymentIntentResponse(
                entity.getPaymentIntentId(),
                entity.getReservationId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private ChargeResponse toChargeResponse(Charge entity) {
        return new ChargeResponse(
                entity.getChargeId(),
                entity.getPaymentIntentId(),
                entity.getProvider(),
                entity.getProviderRef(),
                entity.getCapturedAt()
        );
    }

    private RefundResponse toRefundResponse(Refund entity) {
        return new RefundResponse(
                entity.getRefundId(),
                entity.getChargeId(),
                entity.getAmount(),
                entity.getReason(),
                entity.getCreatedAt()
        );
    }
}