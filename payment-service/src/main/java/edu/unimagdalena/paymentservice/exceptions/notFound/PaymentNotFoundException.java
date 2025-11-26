package edu.unimagdalena.paymentservice.exceptions.notFound;

public class PaymentNotFoundException extends ResourceNotFoundException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
