package edu.unimagdalena.tripservice.exceptions.notFound;

public class DriverNotFoundException extends ResourceNotFoundException {
    public DriverNotFoundException(String message) {
        super(message);
    }
}
