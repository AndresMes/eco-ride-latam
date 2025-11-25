package edu.unimagdalena.paymentservice.domain.enums;

import lombok.Getter;

@Getter
public enum RefundReason {
    /**
     * Cliente solicitó reembolso
     */
    REQUESTED_BY_CUSTOMER("Requested by Customer"),

    /**
     * Viaje cancelado por el conductor
     */
    TRIP_CANCELLED_BY_DRIVER("Trip Cancelled by Driver"),

    /**
     * Viaje cancelado por el pasajero
     */
    TRIP_CANCELLED_BY_PASSENGER("Trip Cancelled by Passenger"),

    /**
     * Pago duplicado
     */
    DUPLICATE_PAYMENT("Duplicate Payment"),

    /**
     * Error en el procesamiento
     */
    PROCESSING_ERROR("Processing Error"),

    /**
     * Compensación del Saga (pago falló después de autorización)
     */
    SAGA_COMPENSATION("Saga Compensation"),

    /**
     * Fraude detectado
     */
    FRAUDULENT("Fraudulent"),

    /**
     * Otra razón
     */
    OTHER("Other");

    private final String displayName;

    RefundReason(String displayName) {
        this.displayName = displayName;
    }

    public boolean isAutomaticCompensation() {
        return this == SAGA_COMPENSATION || this == PROCESSING_ERROR;
    }
}