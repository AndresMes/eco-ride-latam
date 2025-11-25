package edu.unimagdalena.passengerservice.exceptions;

public class DuplicateRatingException extends RuntimeException {
    public DuplicateRatingException(String message) {
        super(message);
    }
}
