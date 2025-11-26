package edu.unimagdalena.paymentservice.shared.util;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * Utilidad para manejar Correlation IDs en transacciones distribuidas
 * El Correlation ID permite rastrear una transacción a través de múltiples microservicios
 */
public class CorrelationIdUtil {

    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    /**
     * Genera un nuevo Correlation ID
     */
    public static String generateCorrelationId() {
        return "COR-" + UUID.randomUUID().toString();
    }

    /**
     * Establece el Correlation ID en el MDC (Mapped Diagnostic Context) de SLF4J
     * Esto permite que aparezca en los logs
     */
    public static void setCorrelationId(String correlationId) {
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = generateCorrelationId();
        }
        MDC.put(CORRELATION_ID_KEY, correlationId);
    }

    /**
     * Obtiene el Correlation ID actual del MDC
     */
    public static String getCorrelationId() {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = generateCorrelationId();
            setCorrelationId(correlationId);
        }
        return correlationId;
    }

    /**
     * Limpia el Correlation ID del MDC
     * Importante llamar esto al final de cada transacción
     */
    public static void clearCorrelationId() {
        MDC.remove(CORRELATION_ID_KEY);
    }

    /**
     * Obtiene el nombre del header HTTP para Correlation ID
     */
    public static String getCorrelationIdHeader() {
        return CORRELATION_ID_HEADER;
    }

    /**
     * Valida si un Correlation ID tiene el formato correcto
     */
    public static boolean isValid(String correlationId) {
        return correlationId != null &&
                !correlationId.isBlank() &&
                correlationId.length() > 4;
    }
}