package edu.unimagdalena.passengerservice.exceptions;

public class LicenseInUseException extends RuntimeException {
    public LicenseInUseException(String message) {
        super(message);
    }
}
