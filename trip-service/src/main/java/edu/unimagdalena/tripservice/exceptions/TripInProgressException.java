package edu.unimagdalena.tripservice.exceptions;

public class TripInProgressException extends RuntimeException {
    public TripInProgressException(String message) {
        super(message);
    }
}
