package edu.unimagdalena.passengerservice.exceptions.notFound;

public class PassengerNotFoundException extends ResourceNotFoundException {
    public PassengerNotFoundException(String message) {
        super(message);
    }
}
