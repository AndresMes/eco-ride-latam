package edu.unimagdalena.paymentservice.domain.enums;

public enum PaymentProvider {
    /**
     * Mock de Stripe para desarrollo/testing
     */
    STRIPE_MOCK("Stripe (Mock)", "stripe_mock"),

    /**
     * Mock de PayPal para desarrollo/testing
     */
    PAYPAL_MOCK("PayPal (Mock)", "paypal_mock"),

    /**
     * Mock de Mercado Pago (popular en LATAM)
     */
    MERCADOPAGO_MOCK("Mercado Pago (Mock)", "mercadopago_mock"),

    /**
     * Proveedor interno/manual
     */
    INTERNAL("Internal", "internal");

    private final String displayName;
    private final String code;

    PaymentProvider(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }

    public static PaymentProvider fromCode(String code) {
        for (PaymentProvider provider : values()) {
            if (provider.code.equalsIgnoreCase(code)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown payment provider code: " + code);
    }
}