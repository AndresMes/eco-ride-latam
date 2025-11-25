package edu.unimagdalena.passengerservice.exceptions;

public class DriverNotAllowedForVerifyingException extends RuntimeException {
    public DriverNotAllowedForVerifyingException(String message) {
        super(message);
    }
}
