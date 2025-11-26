package edu.unimagdalena.paymentservice.presentation.controller;

import edu.unimagdalena.paymentservice.application.dto.request.CapturePaymentRequest;
import edu.unimagdalena.paymentservice.application.dto.request.CreatePaymentIntentRequest;
import edu.unimagdalena.paymentservice.application.dto.request.RefundRequest;
import edu.unimagdalena.paymentservice.application.dto.response.ChargeResponse;
import edu.unimagdalena.paymentservice.application.dto.response.PaymentIntentResponse;
import edu.unimagdalena.paymentservice.application.dto.response.RefundResponse;
import edu.unimagdalena.paymentservice.application.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para operaciones de pago
 *
 * IMPORTANTE: Este controlador es para comunicación interna (service-to-service)
 * Las operaciones de pago normalmente se inician mediante eventos de RabbitMQ,
 * pero estos endpoints están disponibles para:
 * - Testing/debugging
 * - Operaciones administrativas
 * - Consultas de estado
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * POST /api/payments/intent
     * Crea una intención de pago
     *
     * USO: Principalmente para testing. En producción, esto se hace vía eventos.
     */
    @PostMapping("/intent")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @Valid @RequestBody CreatePaymentIntentRequest request) {

        log.info("REST: Creating PaymentIntent for ReservationId: {}", request.getReservationId());
        PaymentIntentResponse response = paymentService.createPaymentIntent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/payments/intent/{id}/authorize
     * Autoriza un pago (reserva fondos)
     */
    @PostMapping("/intent/{id}/authorize")
    public ResponseEntity<PaymentIntentResponse> authorizePayment(
            @PathVariable Long id) {

        log.info("REST: Authorizing payment for PaymentIntentId: {}", id);
        PaymentIntentResponse response = paymentService.authorizePayment(id);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/payments/intent/{id}/capture
     * Captura un pago autorizado (completa la transacción)
     */
    @PostMapping("/intent/{id}/capture")
    public ResponseEntity<ChargeResponse> capturePayment(
            @PathVariable Long id,
            @Valid @RequestBody(required = false) CapturePaymentRequest request) {

        log.info("REST: Capturing payment for PaymentIntentId: {}", id);
        ChargeResponse response = paymentService.capturePayment(id);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/payments/intent/{id}/cancel
     * Cancela un pago
     */
    @PostMapping("/intent/{id}/cancel")
    public ResponseEntity<PaymentIntentResponse> cancelPayment(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Cancelled by user") String reason) {

        log.info("REST: Cancelling payment for PaymentIntentId: {}, Reason: {}", id, reason);
        PaymentIntentResponse response = paymentService.cancelPayment(id, reason);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/payments/refund
     * Procesa un reembolso
     */
    @PostMapping("/refund")
    public ResponseEntity<RefundResponse> processRefund(
            @Valid @RequestBody RefundRequest request) {

        log.info("REST: Processing refund for ChargeId: {}", request.getChargeId());
        RefundResponse response = paymentService.processRefund(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/payments/intent/{id}
     * Obtiene un PaymentIntent por ID
     */
    @GetMapping("/intent/{id}")
    public ResponseEntity<PaymentIntentResponse> getPaymentIntent(
            @PathVariable Long id) {

        log.info("REST: Getting PaymentIntent by id: {}", id);
        PaymentIntentResponse response = paymentService.getPaymentIntent(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/payments/intent/reservation/{reservationId}
     * Obtiene un PaymentIntent por reservationId
     */
    @GetMapping("/intent/reservation/{reservationId}")

    public ResponseEntity<PaymentIntentResponse> getPaymentIntentByReservationId(
            @PathVariable Long reservationId) {

        log.info("REST: Getting PaymentIntent by reservationId: {}", reservationId);
        PaymentIntentResponse response = paymentService.getPaymentIntentByReservationId(reservationId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/payments/charge/{id}
     * Obtiene un Charge por ID
     */
    @GetMapping("/charge/{id}")

    public ResponseEntity<ChargeResponse> getCharge(
            @PathVariable Long id) {

        log.info("REST: Getting Charge by id: {}", id);
        ChargeResponse response = paymentService.getCharge(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/payments/health
     * Health check endpoint (público)
     */
    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(
                HealthResponse.builder()
                        .status("UP")
                        .service("payment-service")
                        .message("Payment Service is running")
                        .build()
        );
    }

    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class HealthResponse {
        private String status;
        private String service;
        private String message;
    }
}