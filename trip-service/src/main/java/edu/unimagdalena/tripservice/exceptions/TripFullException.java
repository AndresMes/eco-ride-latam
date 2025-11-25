package edu.unimagdalena.tripservice.exceptions;

public class TripFullException extends RuntimeException {
    public TripFullException(String message) {
        super(message);
    }
}
