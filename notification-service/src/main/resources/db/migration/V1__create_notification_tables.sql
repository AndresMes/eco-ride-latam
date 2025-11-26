-- ============================================================
-- ECO-RIDE LATAM - NotificationService Database Migration
-- Flyway Migration: V1__create_notification_tables.sql
-- Base de datos: notificationService
-- ============================================================

-- ============================================================
-- Tabla: notifications
-- Almacena registro de todas las notificaciones enviadas
-- ============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    
    -- Identificadores de entidades relacionadas
    reservation_id BIGINT,
    payment_id BIGINT,
    passenger_id BIGINT,
    
    -- Tipo y canal de notificación
    notification_type VARCHAR(100) NOT NULL,
    channel VARCHAR(50) NOT NULL DEFAULT 'EMAIL',
    
    -- Destinatario
    recipient_email VARCHAR(255),
    recipient_phone VARCHAR(20),
    
    -- Contenido
    subject VARCHAR(500),
    body TEXT NOT NULL,
    template_name VARCHAR(100),
    
    -- Estado de envío
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    
    -- Información de envío
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,
    
    -- Proveedor de envío
    provider VARCHAR(50),
    provider_message_id VARCHAR(255),
    provider_response JSONB,
    
    -- Reintentos
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retries INTEGER NOT NULL DEFAULT 3,
    next_retry_at TIMESTAMP,
    
    -- Metadata
    metadata JSONB,
    
    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT notifications_type_check CHECK (
        notification_type IN (
            'RESERVATION_CONFIRMED', 
            'RESERVATION_CANCELLED', 
            'PAYMENT_AUTHORIZED', 
            'PAYMENT_FAILED',
            'TRIP_REMINDER',
            'TRIP_CANCELLED'
        )
    ),
    CONSTRAINT notifications_channel_check CHECK (
        channel IN ('EMAIL', 'SMS', 'PUSH', 'IN_APP')
    ),
    CONSTRAINT notifications_status_check CHECK (
        status IN ('PENDING', 'SENT', 'DELIVERED', 'FAILED', 'CANCELLED')
    )
);

-- Índices para notifications
CREATE INDEX idx_notifications_reservation_id ON notifications(reservation_id);
CREATE INDEX idx_notifications_payment_id ON notifications(payment_id);
CREATE INDEX idx_notifications_passenger_id ON notifications(passenger_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_type ON notifications(notification_type);
CREATE INDEX idx_notifications_channel ON notifications(channel);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
CREATE INDEX idx_notifications_next_retry ON notifications(next_retry_at) 
    WHERE status = 'PENDING' AND next_retry_at IS NOT NULL;

-- ============================================================
-- Tabla: notification_events
-- Registro de eventos recibidos de RabbitMQ (para idempotencia)
-- ============================================================
CREATE TABLE IF NOT EXISTS notification_events (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(255) UNIQUE NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_payload JSONB NOT NULL,
    
    -- Control de procesamiento
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    processed_at TIMESTAMP,
    
    -- Resultado del procesamiento
    notification_id BIGINT,
    processing_result VARCHAR(50),
    error_message TEXT,
    
    -- Timestamps
    received_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_notification_events_notification FOREIGN KEY (notification_id) 
        REFERENCES notifications(id) ON DELETE SET NULL,
    CONSTRAINT notification_events_type_check CHECK (
        event_type IN (
            'ReservationConfirmed', 
            'ReservationCancelled', 
            'PaymentAuthorized', 
            'PaymentFailed'
        )
    ),
    CONSTRAINT notification_events_result_check CHECK (
        processing_result IS NULL OR 
        processing_result IN ('SUCCESS', 'FAILED', 'SKIPPED')
    )
);

-- Índices para notification_events
CREATE INDEX idx_notification_events_event_id ON notification_events(event_id);
CREATE INDEX idx_notification_events_processed ON notification_events(processed) WHERE processed = FALSE;
CREATE INDEX idx_notification_events_type ON notification_events(event_type);
CREATE INDEX idx_notification_events_received_at ON notification_events(received_at);

-- ============================================================
-- Función: update_updated_at_column
-- Actualiza automáticamente el campo updated_at
-- ============================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- Trigger para updated_at
-- ============================================================
CREATE TRIGGER update_notifications_updated_at
    BEFORE UPDATE ON notifications
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();