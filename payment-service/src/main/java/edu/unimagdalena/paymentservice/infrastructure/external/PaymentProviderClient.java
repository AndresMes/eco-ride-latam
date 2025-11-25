package edu.unimagdalena.paymentservice.infrastructure.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

/**
 * Cliente simulado para proveedores de pago externos (Stripe, PayPal, etc.)
 * En producción, esto haría llamadas HTTP reales a las APIs de los proveedores
 */
@Component
@Slf4j
public class PaymentProviderClient {

    @Value("${payment.provider.mock-mode:true}")
    private boolean mockMode;

    @Value("${payment.provider.mock-failure-rate:0.0}")
    private double mockFailureRate;

    private final Random random = new Random();

    /**
     * Simula la autorización de un pago
     * @return Código de autorización
     */
    public String authorizePayment(BigDecimal amount, String currency) {
        log.info("Authorizing payment - Amount: {} {}", amount, currency);

        if (mockMode) {
            simulateProcessingDelay();
            simulateRandomFailure("Authorization");

            String authCode = "AUTH_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            log.info("Payment authorized successfully - AuthCode: {}", authCode);
            return authCode;
        }

        // En producción: llamada HTTP al proveedor real
        throw new UnsupportedOperationException("Real payment provider integration not implemented");
    }

    /**
     * Simula la captura de un pago autorizado
     * @return Referencia del proveedor
     */
    public String capturePayment(BigDecimal amount, String currency) {
        log.info("Capturing payment - Amount: {} {}", amount, currency);

        if (mockMode) {
            simulateProcessingDelay();
            simulateRandomFailure("Capture");

            String providerRef = "ch_" + UUID.randomUUID().toString().substring(0, 16);
            log.info("Payment captured successfully - ProviderRef: {}", providerRef);
            return providerRef;
        }

        // En producción: llamada HTTP al proveedor real
        throw new UnsupportedOperationException("Real payment provider integration not implemented");
    }

    /**
     * Simula el procesamiento de un reembolso
     * @return Referencia del reembolso
     */
    public String refundPayment(BigDecimal amount, String currency) {
        log.info("Processing refund - Amount: {} {}", amount, currency);

        if (mockMode) {
            simulateProcessingDelay();
            simulateRandomFailure("Refund");

            String refundRef = "re_" + UUID.randomUUID().toString().substring(0, 16);
            log.info("Refund processed successfully - RefundRef: {}", refundRef);
            return refundRef;
        }

        // En producción: llamada HTTP al proveedor real
        throw new UnsupportedOperationException("Real payment provider integration not implemented");
    }

    /**
     * Simula un delay de procesamiento (100-500ms)
     */
    private void simulateProcessingDelay() {
        try {
            int delay = 100 + random.nextInt(400);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Processing delay interrupted", e);
        }
    }

    /**
     * Simula fallos aleatorios según la tasa configurada
     */
    private void simulateRandomFailure(String operation) {
        if (mockFailureRate > 0 && random.nextDouble() < mockFailureRate) {
            String[] errorReasons = {
                    "Insufficient funds",
                    "Card declined",
                    "Payment method expired",
                    "Issuer unavailable",
                    "Invalid card number"
            };

            String reason = errorReasons[random.nextInt(errorReasons.length)];
            log.error("Simulated {} failure: {}", operation, reason);
            throw new RuntimeException(operation + " failed: " + reason);
        }
    }
}