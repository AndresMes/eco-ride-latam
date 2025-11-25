-- V4__seed_test_data.sql (OPCIONAL - Solo para desarrollo)
-- Datos de prueba para testing local

-- NOTA: Este archivo es OPCIONAL y solo debe ejecutarse en entornos de desarrollo
-- Comentar o eliminar en producción

-- PaymentIntent de prueba #1 - Pago exitoso capturado
INSERT INTO payment_intent (
    reservation_id,
    amount,
    currency,
    status,
    idempotency_key,
    correlation_id,
    description,
    created_at,
    updated_at
) VALUES (
             1001,
             25.50,
             'USD',
             'CAPTURED',
             'test-idempotency-001',
             'test-correlation-001',
             'Pago de prueba para reserva 1001',
             CURRENT_TIMESTAMP - INTERVAL '2 days',
             CURRENT_TIMESTAMP - INTERVAL '2 days'
         );

-- Charge asociado al PaymentIntent #1
INSERT INTO charge (
    payment_intent_id,
    provider,
    provider_reference,
    amount,
    currency,
    authorization_code,
    captured_at,
    description,
    created_at
) VALUES (
             1,
             'STRIPE_MOCK',
             'ch_test_12345',
             25.50,
             'USD',
             'AUTH_12345',
             CURRENT_TIMESTAMP - INTERVAL '2 days',
             'Cargo de prueba',
             CURRENT_TIMESTAMP - INTERVAL '2 days'
         );

-- PaymentIntent de prueba #2 - Pago fallido
INSERT INTO payment_intent (
    reservation_id,
    amount,
    currency,
    status,
    idempotency_key,
    correlation_id,
    description,
    failure_reason,
    created_at,
    updated_at
) VALUES (
             1002,
             15.00,
             'USD',
             'FAILED',
             'test-idempotency-002',
             'test-correlation-002',
             'Pago fallido para pruebas de Saga',
             'Insufficient funds (simulated)',
             CURRENT_TIMESTAMP - INTERVAL '1 day',
             CURRENT_TIMESTAMP - INTERVAL '1 day'
         );

-- PaymentIntent de prueba #3 - Pago autorizado pero no capturado
INSERT INTO payment_intent (
    reservation_id,
    amount,
    currency,
    status,
    idempotency_key,
    correlation_id,
    description,
    created_at,
    updated_at
) VALUES (
             1003,
             30.00,
             'USD',
             'AUTHORIZED',
             'test-idempotency-003',
             'test-correlation-003',
             'Pago autorizado pendiente de captura',
             CURRENT_TIMESTAMP - INTERVAL '3 hours',
             CURRENT_TIMESTAMP - INTERVAL '3 hours'
         );

-- Charge asociado al PaymentIntent #3
INSERT INTO charge (
    payment_intent_id,
    provider,
    provider_reference,
    amount,
    currency,
    authorization_code,
    created_at
) VALUES (
             3,
             'MERCADOPAGO_MOCK',
             'ch_test_67890',
             30.00,
             'USD',
             'AUTH_67890',
             CURRENT_TIMESTAMP - INTERVAL '3 hours'
         );

-- PaymentIntent de prueba #4 - Con reembolso (compensación del Saga)
INSERT INTO payment_intent (
    reservation_id,
    amount,
    currency,
    status,
    idempotency_key,
    correlation_id,
    description,
    created_at,
    updated_at
) VALUES (
             1004,
             20.00,
             'USD',
             'CAPTURED',
             'test-idempotency-004',
             'test-correlation-004',
             'Pago con reembolso por compensación',
             CURRENT_TIMESTAMP - INTERVAL '5 hours',
             CURRENT_TIMESTAMP - INTERVAL '5 hours'
         );

-- Charge asociado al PaymentIntent #4
INSERT INTO charge (
    payment_intent_id,
    provider,
    provider_reference,
    amount,
    currency,
    authorization_code,
    captured_at,
    created_at
) VALUES (
             4,
             'PAYPAL_MOCK',
             'ch_test_99999',
             20.00,
             'USD',
             'AUTH_99999',
             CURRENT_TIMESTAMP - INTERVAL '5 hours',
             CURRENT_TIMESTAMP - INTERVAL '5 hours'
         );

-- Refund de compensación del Saga
INSERT INTO refund (
    charge_id,
    amount,
    currency,
    reason,
    reason_details,
    provider_reference,
    idempotency_key,
    processed_at,
    created_at
) VALUES (
             4,
             20.00,
             'USD',
             'SAGA_COMPENSATION',
             'Viaje cancelado después del pago - compensación automática',
             'rf_test_saga_001',
             'refund-idempotency-001',
             CURRENT_TIMESTAMP - INTERVAL '4 hours',
             CURRENT_TIMESTAMP - INTERVAL '4 hours'
         );

-- Mensaje de confirmación
DO $$
BEGIN
    RAISE NOTICE 'Test data inserted successfully for development environment';
END $$;