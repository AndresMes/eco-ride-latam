package edu.unimagdalena.paymentservice.shared.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

/**
 * Utilidad para generar y validar claves de idempotencia
 * La idempotencia asegura que operaciones repetidas tengan el mismo efecto que una sola operación
 */
public class IdempotencyUtil {

    /**
     * Genera una clave de idempotencia basada en un prefijo y datos
     */
    public static String generateIdempotencyKey(String prefix, String... data) {
        StringBuilder sb = new StringBuilder(prefix);
        for (String datum : data) {
            if (datum != null) {
                sb.append("-").append(datum);
            }
        }
        sb.append("-").append(UUID.randomUUID().toString().substring(0, 8));
        return sb.toString();
    }

    /**
     * Genera una clave de idempotencia simple con UUID
     */
    public static String generateSimpleKey() {
        return "IDEM-" + UUID.randomUUID().toString();
    }

    /**
     * Genera una clave de idempotencia para pagos
     */
    public static String generatePaymentIdempotencyKey(Long reservationId) {
        return generateIdempotencyKey("payment", reservationId.toString());
    }

    /**
     * Genera una clave de idempotencia para reembolsos
     */
    public static String generateRefundIdempotencyKey(Long chargeId) {
        return generateIdempotencyKey("refund", chargeId.toString());
    }

    /**
     * Genera un hash SHA-256 de los datos para usar como clave de idempotencia
     * Útil cuando se quiere generar la misma clave para los mismos datos
     */
    public static String generateDeterministicKey(String... data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder combined = new StringBuilder();
            for (String datum : data) {
                if (datum != null) {
                    combined.append(datum);
                }
            }

            byte[] hash = digest.digest(combined.toString().getBytes(StandardCharsets.UTF_8));
            return "IDEM-" + Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 16);

        } catch (NoSuchAlgorithmException e) {
            // Fallback a UUID si SHA-256 no está disponible
            return generateSimpleKey();
        }
    }

    /**
     * Valida el formato de una clave de idempotencia
     */
    public static boolean isValidIdempotencyKey(String key) {
        return key != null &&
                !key.isBlank() &&
                key.length() >= 10 &&
                key.length() <= 100;
    }

    /**
     * Sanitiza una clave de idempotencia removiendo caracteres no permitidos
     */
    public static String sanitize(String key) {
        if (key == null) {
            return null;
        }
        return key.replaceAll("[^a-zA-Z0-9-_]", "").substring(0, Math.min(key.length(), 100));
    }
}