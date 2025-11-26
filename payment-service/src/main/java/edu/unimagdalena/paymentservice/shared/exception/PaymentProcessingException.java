package edu.unimagdalena.paymentservice.shared.exception;

/**
 * Excepción lanzada cuando ocurre un error durante el procesamiento de un pago
 */
public class PaymentProcessingException extends RuntimeException {

    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentProcessingException(Throwable cause) {
        super(cause);
    }
}