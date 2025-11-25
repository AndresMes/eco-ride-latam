package edu.unimagdalena.paymentservice.application.service;

import edu.unimagdalena.paymentservice.application.dto.request.CreatePaymentIntentRequest;
import edu.unimagdalena.paymentservice.application.dto.request.RefundRequest;
import edu.unimagdalena.paymentservice.application.dto.response.ChargeResponse;
import edu.unimagdalena.paymentservice.application.dto.response.PaymentIntentResponse;
import edu.unimagdalena.paymentservice.application.dto.response.RefundResponse;
import edu.unimagdalena.paymentservice.domain.enums.PaymentProvider;
import edu.unimagdalena.paymentservice.domain.enums.PaymentStatus;
import edu.unimagdalena.paymentservice.domain.model.Charge;
import edu.unimagdalena.paymentservice.domain.model.PaymentIntent;
import edu.unimagdalena.paymentservice.domain.model.Refund;
import edu.unimagdalena.paymentservice.domain.repository.ChargeRepository;
import edu.unimagdalena.paymentservice.domain.repository.PaymentIntentRepository;
import edu.unimagdalena.paymentservice.domain.repository.RefundRepository;
import edu.unimagdalena.paymentservice.infrastructure.external.PaymentProviderClient;
import edu.unimagdalena.paymentservice.shared.exception.InvalidPaymentStateException;
import edu.unimagdalena.paymentservice.shared.exception.PaymentNotFoundException;
import edu.unimagdalena.paymentservice.shared.exception.PaymentProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentIntentRepository paymentIntentRepository;
    private final ChargeRepository chargeRepository;
    private final RefundRepository refundRepository;
    private final PaymentProviderClient paymentProviderClient;

    @Override
    @Transactional
    public PaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) {
        log.info("Creating PaymentIntent for ReservationId: {}", request.getReservationId());

        // Verificar idempotencia
        if (request.getIdempotencyKey() != null) {
            PaymentIntent existing = paymentIntentRepository
                    .findByIdempotencyKey(request.getIdempotencyKey())
                    .orElse(null);
            if (existing != null) {
                log.info("PaymentIntent already exists with idempotency key: {}",
                        request.getIdempotencyKey());
                return mapToResponse(existing);
            }
        }

        // Verificar si ya existe un pago para esta reserva
        if (paymentIntentRepository.existsByReservationId(request.getReservationId())) {
            throw new PaymentProcessingException(
                    "PaymentIntent already exists for ReservationId: " + request.getReservationId());
        }

        // Crear PaymentIntent
        PaymentIntent paymentIntent = PaymentIntent.builder()
                .reservationId(request.getReservationId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.REQUIRES_ACTION)
                .description(request.getDescription())
                .idempotencyKey(request.getIdempotencyKey())
                .correlationId(request.getCorrelationId())
                .build();

        paymentIntent = paymentIntentRepository.save(paymentIntent);

        log.info("PaymentIntent created successfully - ID: {}, ReservationId: {}",
                paymentIntent.getId(), paymentIntent.getReservationId());

        return mapToResponse(paymentIntent);
    }

    @Override
    @Transactional
    public PaymentIntentResponse authorizePayment(Long paymentIntentId) {
        log.info("Authorizing payment for PaymentIntentId: {}", paymentIntentId);

        PaymentIntent paymentIntent = findPaymentIntentById(paymentIntentId);

        if (!paymentIntent.isProcessable()) {
            throw new InvalidPaymentStateException(
                    "Cannot authorize payment in status: " + paymentIntent.getStatus());
        }

        try {
            // Simular llamada al proveedor de pagos
            String authCode = paymentProviderClient.authorizePayment(
                    paymentIntent.getAmount(),
                    paymentIntent.getCurrency()
            );

            // Actualizar estado
            paymentIntent.authorize();
            paymentIntent = paymentIntentRepository.save(paymentIntent);

            log.info("Payment authorized successfully - PaymentIntentId: {}, AuthCode: {}",
                    paymentIntentId, authCode);

            return mapToResponse(paymentIntent);

        } catch (Exception e) {
            log.error("Payment authorization failed - PaymentIntentId: {}, Error: {}",
                    paymentIntentId, e.getMessage());
            paymentIntent.fail("Authorization failed: " + e.getMessage());
            paymentIntentRepository.save(paymentIntent);
            throw new PaymentProcessingException("Payment authorization failed", e);
        }
    }

    @Override
    @Transactional
    public ChargeResponse capturePayment(Long paymentIntentId) {
        log.info("Capturing payment for PaymentIntentId: {}", paymentIntentId);

        PaymentIntent paymentIntent = findPaymentIntentById(paymentIntentId);

        if (paymentIntent.getStatus() != PaymentStatus.AUTHORIZED) {
            throw new InvalidPaymentStateException(
                    "Cannot capture payment in status: " + paymentIntent.getStatus());
        }

        try {
            // Simular llamada al proveedor de pagos
            String providerRef = paymentProviderClient.capturePayment(
                    paymentIntent.getAmount(),
                    paymentIntent.getCurrency()
            );

            // Crear Charge
            Charge charge = Charge.builder()
                    .paymentIntent(paymentIntent)
                    .provider(PaymentProvider.STRIPE_MOCK)
                    .providerReference(providerRef)
                    .amount(paymentIntent.getAmount())
                    .currency(paymentIntent.getCurrency())
                    .authorizationCode(UUID.randomUUID().toString())
                    .description("Charge for reservation " + paymentIntent.getReservationId())
                    .build();

            charge = chargeRepository.save(charge);

            // Actualizar PaymentIntent
            paymentIntent.capture();
            paymentIntentRepository.save(paymentIntent);

            log.info("Payment captured successfully - PaymentIntentId: {}, ChargeId: {}",
                    paymentIntentId, charge.getId());

            return mapToChargeResponse(charge);

        } catch (Exception e) {
            log.error("Payment capture failed - PaymentIntentId: {}, Error: {}",
                    paymentIntentId, e.getMessage());
            paymentIntent.fail("Capture failed: " + e.getMessage());
            paymentIntentRepository.save(paymentIntent);
            throw new PaymentProcessingException("Payment capture failed", e);
        }
    }

    @Override
    @Transactional
    public PaymentIntentResponse cancelPayment(Long paymentIntentId, String reason) {
        log.info("Cancelling payment for PaymentIntentId: {}, Reason: {}", paymentIntentId, reason);

        PaymentIntent paymentIntent = findPaymentIntentById(paymentIntentId);
        paymentIntent.cancel();
        paymentIntent.setFailureReason(reason);
        paymentIntent = paymentIntentRepository.save(paymentIntent);

        log.info("Payment cancelled successfully - PaymentIntentId: {}", paymentIntentId);

        return mapToResponse(paymentIntent);
    }

    @Override
    @Transactional
    public RefundResponse processRefund(RefundRequest request) {
        log.info("Processing refund for ChargeId: {}, Amount: {}",
                request.getChargeId(), request.getAmount());

        Charge charge = chargeRepository.findById(request.getChargeId())
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Charge not found with ID: " + request.getChargeId()));

        if (!charge.isRefundable()) {
            throw new InvalidPaymentStateException("Charge is not refundable");
        }

        // Verificar idempotencia
        if (request.getIdempotencyKey() != null) {
            Refund existing = refundRepository
                    .findByIdempotencyKey(request.getIdempotencyKey())
                    .orElse(null);
            if (existing != null) {
                log.info("Refund already exists with idempotency key: {}",
                        request.getIdempotencyKey());
                return mapToRefundResponse(existing);
            }
        }

        try {
            // Simular llamada al proveedor
            String providerRef = paymentProviderClient.refundPayment(
                    request.getAmount(),
                    charge.getCurrency()
            );

            Refund refund = Refund.builder()
                    .charge(charge)
                    .amount(request.getAmount())
                    .currency(charge.getCurrency())
                    .reason(request.getReason())
                    .reasonDetails(request.getReasonDetails())
                    .providerReference(providerRef)
                    .idempotencyKey(request.getIdempotencyKey())
                    .build();

            refund = refundRepository.save(refund);

            log.info("Refund processed successfully - RefundId: {}, ChargeId: {}",
                    refund.getId(), charge.getId());

            return mapToRefundResponse(refund);

        } catch (Exception e) {
            log.error("Refund processing failed - ChargeId: {}, Error: {}",
                    request.getChargeId(), e.getMessage());
            throw new PaymentProcessingException("Refund processing failed", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentIntentResponse getPaymentIntent(Long id) {
        PaymentIntent paymentIntent = findPaymentIntentById(id);
        return mapToResponse(paymentIntent);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentIntentResponse getPaymentIntentByReservationId(Long reservationId) {
        PaymentIntent paymentIntent = paymentIntentRepository
                .findByReservationId(reservationId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "PaymentIntent not found for ReservationId: " + reservationId));
        return mapToResponse(paymentIntent);
    }

    @Override
    @Transactional(readOnly = true)
    public ChargeResponse getCharge(Long id) {
        Charge charge = chargeRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Charge not found with ID: " + id));
        return mapToChargeResponse(charge);
    }

    // Helper methods
    private PaymentIntent findPaymentIntentById(Long id) {
        return paymentIntentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "PaymentIntent not found with ID: " + id));
    }

    private PaymentIntentResponse mapToResponse(PaymentIntent paymentIntent) {
        return PaymentIntentResponse.builder()
                .id(paymentIntent.getId())
                .reservationId(paymentIntent.getReservationId())
                .amount(paymentIntent.getAmount())
                .currency(paymentIntent.getCurrency())
                .status(paymentIntent.getStatus())
                .description(paymentIntent.getDescription())
                .failureReason(paymentIntent.getFailureReason())
                .correlationId(paymentIntent.getCorrelationId())
                .createdAt(paymentIntent.getCreatedAt())
                .updatedAt(paymentIntent.getUpdatedAt())
                .build();
    }

    private ChargeResponse mapToChargeResponse(Charge charge) {
        return ChargeResponse.builder()
                .id(charge.getId())
                .paymentIntentId(charge.getPaymentIntent().getId())
                .provider(charge.getProvider())
                .providerReference(charge.getProviderReference())
                .amount(charge.getAmount())
                .currency(charge.getCurrency())
                .authorizationCode(charge.getAuthorizationCode())
                .capturedAt(charge.getCapturedAt())
                .description(charge.getDescription())
                .createdAt(charge.getCreatedAt())
                .build();
    }

    private RefundResponse mapToRefundResponse(Refund refund) {
        return RefundResponse.builder()
                .id(refund.getId())
                .chargeId(refund.getCharge().getId())
                .amount(refund.getAmount())
                .currency(refund.getCurrency())
                .reason(refund.getReason())
                .reasonDetails(refund.getReasonDetails())
                .providerReference(refund.getProviderReference())
                .processedAt(refund.getProcessedAt())
                .createdAt(refund.getCreatedAt())
                .build();
    }
}