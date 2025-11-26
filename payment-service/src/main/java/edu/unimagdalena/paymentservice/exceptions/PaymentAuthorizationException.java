package edu.unimagdalena.paymentservice.exceptions;

public class PaymentAuthorizationException extends RuntimeException {
    public PaymentAuthorizationException(String message) {
        super(message);
    }
}
