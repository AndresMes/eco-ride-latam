package edu.unimagdalena.passengerservice.exceptions;

public class PassengerAlreadyExistsException extends RuntimeException {
    public PassengerAlreadyExistsException(String message) {
        super(message);
    }
}
