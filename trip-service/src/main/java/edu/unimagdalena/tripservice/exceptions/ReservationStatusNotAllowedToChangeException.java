package edu.unimagdalena.tripservice.exceptions;

public class ReservationStatusNotAllowedToChangeException extends RuntimeException {
    public ReservationStatusNotAllowedToChangeException(String message) {
        super(message);
    }
}
