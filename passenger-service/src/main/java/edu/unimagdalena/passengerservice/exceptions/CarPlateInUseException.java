package edu.unimagdalena.passengerservice.exceptions;

public class CarPlateInUseException extends RuntimeException {
    public CarPlateInUseException(String message) {
        super(message);
    }
}
