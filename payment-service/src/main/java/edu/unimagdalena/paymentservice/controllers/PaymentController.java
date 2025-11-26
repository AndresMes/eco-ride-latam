package edu.unimagdalena.paymentservice.controllers;

import edu.unimagdalena.paymentservice.dtos.requests.PaymentIntentRequest;
import edu.unimagdalena.paymentservice.dtos.responses.ChargeResponse;
import edu.unimagdalena.paymentservice.dtos.responses.PaymentIntentResponse;
import edu.unimagdalena.paymentservice.dtos.responses.RefundResponse;
import edu.unimagdalena.paymentservice.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/intent")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PaymentIntentResponse> createPaymentIntent(
            @Valid @RequestBody PaymentIntentRequest request) {

        return paymentService.createPaymentIntent(request);
    }

    @GetMapping("/intent/{paymentIntentId}")
    public Mono<ResponseEntity<PaymentIntentResponse>> getPaymentIntent(
            @PathVariable Long paymentIntentId) {

        return paymentService.getPaymentIntent(paymentIntentId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/intent/reservation/{reservationId}")
    public Mono<ResponseEntity<PaymentIntentResponse>> getPaymentIntentByReservation(
            @PathVariable Long reservationId) {

        return paymentService.getPaymentIntentByReservation(reservationId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/capture/{paymentIntentId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ChargeResponse> capturePayment(@PathVariable Long paymentIntentId) {
        log.info("Capturing payment for intent: {}", paymentIntentId);
        return paymentService.authorizeAndCapture(paymentIntentId);
    }

    @GetMapping("/charge/{chargeId}")
    public Mono<ResponseEntity<ChargeResponse>> getCharge(@PathVariable Long chargeId) {
        log.debug("Getting charge: {}", chargeId);
        return paymentService.getCharge(chargeId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    @PostMapping("/refund/{chargeId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<RefundResponse> refundPayment(
            @PathVariable Long chargeId,
            @RequestParam(required = false, defaultValue = "Reservation cancelled") String reason) {

        log.info("Processing refund for charge: {} - reason: {}", chargeId, reason);
        return paymentService.refund(chargeId, reason);
    }

    @GetMapping("/refund/{refundId}")
    public Mono<ResponseEntity<RefundResponse>> getRefund(@PathVariable Long refundId) {
        log.debug("Getting refund: {}", refundId);
        return paymentService.getRefund(refundId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}