package edu.unimagdalena.passengerservice.exceptions.notFound;

public class DriverNotFoundException extends ResourceNotFoundException {
    public DriverNotFoundException(String message) {
        super(message);
    }
}
