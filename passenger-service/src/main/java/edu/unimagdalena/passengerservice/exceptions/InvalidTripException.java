package edu.unimagdalena.passengerservice.exceptions;

public class InvalidTripException extends RuntimeException {
    public InvalidTripException(String message) {
        super(message);
    }
}
