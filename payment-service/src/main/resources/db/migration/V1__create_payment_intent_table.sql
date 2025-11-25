-- V1__create_payment_intent_table.sql
-- Tabla principal de intenciones de pago

CREATE TABLE payment_intent (
                                id BIGSERIAL PRIMARY KEY,
                                reservation_id BIGINT NOT NULL UNIQUE,
                                amount NUMERIC(10, 2) NOT NULL CHECK (amount > 0),
                                currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                                status VARCHAR(50) NOT NULL,
                                idempotency_key VARCHAR(100) UNIQUE,
                                correlation_id VARCHAR(100),
                                description VARCHAR(500),
                                failure_reason VARCHAR(500),
                                metadata TEXT,
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT chk_status CHECK (status IN ('REQUIRES_ACTION', 'AUTHORIZED', 'CAPTURED', 'FAILED', 'CANCELLED')),
                                CONSTRAINT chk_currency CHECK (LENGTH(currency) = 3)
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_reservation_id ON payment_intent(reservation_id);
CREATE INDEX idx_status ON payment_intent(status);
CREATE INDEX idx_created_at ON payment_intent(created_at);
CREATE INDEX idx_correlation_id ON payment_intent(correlation_id);
CREATE INDEX idx_idempotency_key ON payment_intent(idempotency_key);

-- Comentarios para documentación
COMMENT ON TABLE payment_intent IS 'Intenciones de pago para reservas de viajes';
COMMENT ON COLUMN payment_intent.reservation_id IS 'ID de la reserva en TripService';
COMMENT ON COLUMN payment_intent.idempotency_key IS 'Clave para evitar pagos duplicados';
COMMENT ON COLUMN payment_intent.correlation_id IS 'ID para rastrear transacciones distribuidas (Saga)';
COMMENT ON COLUMN payment_intent.status IS 'Estado del pago: REQUIRES_ACTION, AUTHORIZED, CAPTURED, FAILED, CANCELLED';