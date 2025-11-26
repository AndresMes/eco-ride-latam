-- V3__create_refund_table.sql
-- Tabla de reembolsos (compensaciones del Saga y solicitudes de clientes)

CREATE TABLE refund (
                        id BIGSERIAL PRIMARY KEY,
                        charge_id BIGINT NOT NULL,
                        amount NUMERIC(10, 2) NOT NULL CHECK (amount > 0),
                        currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                        reason VARCHAR(50) NOT NULL,
                        reason_details VARCHAR(500),
                        provider_reference VARCHAR(100) UNIQUE,
                        idempotency_key VARCHAR(100) UNIQUE,
                        processed_at TIMESTAMP,
                        provider_metadata TEXT,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT fk_refund_charge
                            FOREIGN KEY (charge_id)
                                REFERENCES charge(id)
                                ON DELETE CASCADE,

                        CONSTRAINT chk_reason CHECK (reason IN (
                                                                'REQUESTED_BY_CUSTOMER',
                                                                'TRIP_CANCELLED_BY_DRIVER',
                                                                'TRIP_CANCELLED_BY_PASSENGER',
                                                                'DUPLICATE_PAYMENT',
                                                                'PROCESSING_ERROR',
                                                                'SAGA_COMPENSATION',
                                                                'FRAUDULENT',
                                                                'OTHER'
                            )),
                        CONSTRAINT chk_currency CHECK (LENGTH(currency) = 3)
);

-- Índices para mejorar el rendimiento
-- NOTA: Cambiados los nombres de los índices para evitar duplicados
CREATE INDEX idx_refund_charge_id ON refund(charge_id);
CREATE INDEX idx_refund_created_at ON refund(created_at);
CREATE INDEX idx_refund_reason ON refund(reason);
CREATE INDEX idx_refund_idempotency_key ON refund(idempotency_key);
CREATE INDEX idx_refund_processed_at ON refund(processed_at);

-- Comentarios para documentación
COMMENT ON TABLE refund IS 'Reembolsos procesados (compensaciones del Saga y solicitudes de clientes)';
COMMENT ON COLUMN refund.charge_id IS 'Referencia al Charge que se está reembolsando';
COMMENT ON COLUMN refund.reason IS 'Razón del reembolso';
COMMENT ON COLUMN refund.idempotency_key IS 'Clave para evitar reembolsos duplicados';
COMMENT ON COLUMN refund.processed_at IS 'Momento en que se procesó el reembolso';
COMMENT ON COLUMN refund.provider_reference IS 'ID de la transacción de reembolso en el proveedor';

-- Vista útil para reportes: Total reembolsado por charge
CREATE OR REPLACE VIEW v_charge_refund_summary AS
SELECT
    c.id AS charge_id,
    c.payment_intent_id,
    c.amount AS charge_amount,
    COALESCE(SUM(r.amount), 0) AS total_refunded,
    c.amount - COALESCE(SUM(r.amount), 0) AS net_amount,
    COUNT(r.id) AS refund_count
FROM charge c
         LEFT JOIN refund r ON r.charge_id = c.id
GROUP BY c.id, c.payment_intent_id, c.amount;

COMMENT ON VIEW v_charge_refund_summary IS 'Resumen de reembolsos por cargo';