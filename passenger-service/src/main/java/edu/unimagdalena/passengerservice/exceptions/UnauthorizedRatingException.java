package edu.unimagdalena.passengerservice.exceptions;

public class UnauthorizedRatingException extends RuntimeException {
    public UnauthorizedRatingException(String message) {
        super(message);
    }
}
