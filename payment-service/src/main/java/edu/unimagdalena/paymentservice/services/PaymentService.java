package edu.unimagdalena.paymentservice.services;

import edu.unimagdalena.paymentservice.dtos.requests.PaymentIntentRequest;
import edu.unimagdalena.paymentservice.dtos.responses.ChargeResponse;
import edu.unimagdalena.paymentservice.dtos.responses.PaymentIntentResponse;
import edu.unimagdalena.paymentservice.dtos.responses.RefundResponse;
import edu.unimagdalena.paymentservice.events.ReservationRequestedEvent;
import reactor.core.publisher.Mono;


public interface PaymentService {


    Mono<Void> processPaymentForReservation(ReservationRequestedEvent event);

    Mono<Void> publishPaymentFailed(Long reservationId, String reason);

    Mono<PaymentIntentResponse> createPaymentIntent(PaymentIntentRequest request);

    Mono<PaymentIntentResponse> getPaymentIntent(Long paymentIntentId);

    Mono<PaymentIntentResponse> getPaymentIntentByReservation(Long reservationId);

    Mono<ChargeResponse> authorizeAndCapture(Long paymentIntentId);

    Mono<ChargeResponse> getCharge(Long chargeId);

    Mono<RefundResponse> refund(Long chargeId, String reason);

    Mono<RefundResponse> getRefund(Long refundId);
}
