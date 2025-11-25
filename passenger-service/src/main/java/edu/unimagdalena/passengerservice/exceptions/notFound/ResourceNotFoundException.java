package edu.unimagdalena.passengerservice.exceptions.notFound;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
