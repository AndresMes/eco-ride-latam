package edu.unimagdalena.tripservice.exceptions.notFound;

public class ReservationNotFoundException extends ResourceNotFoundException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
