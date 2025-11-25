package edu.unimagdalena.paymentservice.application.service;

import edu.unimagdalena.paymentservice.application.dto.request.CreatePaymentIntentRequest;
import edu.unimagdalena.paymentservice.application.dto.request.RefundRequest;
import edu.unimagdalena.paymentservice.application.dto.response.ChargeResponse;
import edu.unimagdalena.paymentservice.application.dto.response.PaymentIntentResponse;
import edu.unimagdalena.paymentservice.application.dto.response.RefundResponse;

public interface PaymentService {

    /**
     * Crea una intención de pago
     */
    PaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request);

    /**
     * Autoriza un pago (reserva fondos)
     */
    PaymentIntentResponse authorizePayment(Long paymentIntentId);

    /**
     * Captura un pago autorizado (completa la transacción)
     */
    ChargeResponse capturePayment(Long paymentIntentId);

    /**
     * Cancela un pago (solo si está en estado REQUIRES_ACTION o AUTHORIZED)
     */
    PaymentIntentResponse cancelPayment(Long paymentIntentId, String reason);

    /**
     * Procesa un reembolso
     */
    RefundResponse processRefund(RefundRequest request);

    /**
     * Obtiene un PaymentIntent por ID
     */
    PaymentIntentResponse getPaymentIntent(Long id);

    /**
     * Obtiene un PaymentIntent por reservationId
     */
    PaymentIntentResponse getPaymentIntentByReservationId(Long reservationId);

    /**
     * Obtiene un Charge por ID
     */
    ChargeResponse getCharge(Long id);
}