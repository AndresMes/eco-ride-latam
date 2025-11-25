package edu.unimagdalena.paymentservice.domain.enums;

public enum PaymentStatus {
    /**
     * Pago creado, esperando procesamiento
     */
    REQUIRES_ACTION,

    /**
     * Pago autorizado (fondos reservados pero no capturados)
     */
    AUTHORIZED,

    /**
     * Pago capturado exitosamente (transacción completa)
     */
    CAPTURED,

    /**
     * Pago falló durante autorización o captura
     */
    FAILED,

    /**
     * Pago cancelado antes de ser capturado
     */
    CANCELLED;

    public boolean isSuccess() {
        return this == AUTHORIZED || this == CAPTURED;
    }

    public boolean isFailed() {
        return this == FAILED;
    }

    public boolean isPending() {
        return this == REQUIRES_ACTION;
    }

    public boolean isTerminal() {
        return this == CAPTURED || this == FAILED || this == CANCELLED;
    }
}