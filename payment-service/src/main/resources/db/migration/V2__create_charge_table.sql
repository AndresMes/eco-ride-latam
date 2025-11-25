-- V2__create_charge_table.sql
-- Tabla de cargos (transacciones procesadas con proveedores)

CREATE TABLE charge (
                        id BIGSERIAL PRIMARY KEY,
                        payment_intent_id BIGINT NOT NULL,
                        provider VARCHAR(50) NOT NULL,
                        provider_reference VARCHAR(100) UNIQUE,
                        amount NUMERIC(10, 2) NOT NULL CHECK (amount > 0),
                        currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                        authorization_code VARCHAR(50),
                        captured_at TIMESTAMP,
                        description VARCHAR(500),
                        provider_metadata TEXT,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT fk_charge_payment_intent
                            FOREIGN KEY (payment_intent_id)
                                REFERENCES payment_intent(id)
                                ON DELETE CASCADE,

                        CONSTRAINT chk_provider CHECK (provider IN ('STRIPE_MOCK', 'PAYPAL_MOCK', 'MERCADOPAGO_MOCK', 'INTERNAL')),
                        CONSTRAINT chk_currency CHECK (LENGTH(currency) = 3)
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_payment_intent_id ON charge(payment_intent_id);
CREATE INDEX idx_provider_ref ON charge(provider_reference);
CREATE INDEX idx_captured_at ON charge(captured_at);
CREATE INDEX idx_provider ON charge(provider);

-- Comentarios para documentación
COMMENT ON TABLE charge IS 'Cargos procesados con proveedores de pago';
COMMENT ON COLUMN charge.payment_intent_id IS 'Referencia al PaymentIntent';
COMMENT ON COLUMN charge.provider IS 'Proveedor de pago utilizado';
COMMENT ON COLUMN charge.provider_reference IS 'ID de transacción del proveedor externo';
COMMENT ON COLUMN charge.authorization_code IS 'Código de autorización del proveedor';
COMMENT ON COLUMN charge.captured_at IS 'Momento en que se capturó el pago';