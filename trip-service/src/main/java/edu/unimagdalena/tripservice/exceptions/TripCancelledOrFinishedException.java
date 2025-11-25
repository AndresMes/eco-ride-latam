package edu.unimagdalena.tripservice.exceptions;

public class TripCancelledOrFinishedException extends RuntimeException {
    public TripCancelledOrFinishedException(String message) {
        super(message);
    }
}
