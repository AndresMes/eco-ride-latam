package edu.unimagdalena.tripservice.exceptions;

public class TripStatusUpdateNotAllowedException extends RuntimeException {
    public TripStatusUpdateNotAllowedException(String message) {
        super(message);
    }
}
