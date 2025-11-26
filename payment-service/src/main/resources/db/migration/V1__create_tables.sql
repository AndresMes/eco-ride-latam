-- Payment Intent Table
CREATE TABLE IF NOT EXISTS payment_intents (
                                               payment_intent_id BIGSERIAL PRIMARY KEY,
                                               reservation_id BIGINT NOT NULL UNIQUE,
                                               amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
                                               currency VARCHAR(3) NOT NULL DEFAULT 'COP',
                                               status VARCHAR(50) NOT NULL,
                                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- Charge Table
CREATE TABLE IF NOT EXISTS charges (
                                       charge_id BIGSERIAL PRIMARY KEY,
                                       payment_intent_id BIGINT NOT NULL,
                                       provider VARCHAR(50) NOT NULL,
                                       provider_ref VARCHAR(255),
                                       captured_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       CONSTRAINT fk_charge_payment_intent FOREIGN KEY (payment_intent_id)
                                           REFERENCES payment_intents(payment_intent_id) ON DELETE CASCADE
);


-- Refund Table
CREATE TABLE IF NOT EXISTS refunds (
                                       refund_id BIGSERIAL PRIMARY KEY,
                                       charge_id BIGINT NOT NULL,
                                       amount DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
                                       reason TEXT,
                                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       CONSTRAINT fk_refund_charge FOREIGN KEY (charge_id)
                                           REFERENCES charges(charge_id) ON DELETE CASCADE
);
