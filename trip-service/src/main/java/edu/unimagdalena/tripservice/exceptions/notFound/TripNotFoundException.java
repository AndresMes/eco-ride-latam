package edu.unimagdalena.tripservice.exceptions.notFound;

public class TripNotFoundException extends ResourceNotFoundException {
    public TripNotFoundException(String message) {
        super(message);
    }
}
