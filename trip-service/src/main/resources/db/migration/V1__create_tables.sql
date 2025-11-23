-- 1) Tabla trips
CREATE TABLE IF NOT EXISTS trips (
                                     trip_id BIGSERIAL PRIMARY KEY,
                                     driver_id BIGINT,
                                     origin TEXT NOT NULL,
                                     destination TEXT NOT NULL,
                                     start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                     seats_available BIGINT NOT NULL CHECK (seats_available >= 0),
    price NUMERIC(10,2) NOT NULL,
    status VARCHAR(250) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
    );


-- 2) Tabla reservations
CREATE TABLE IF NOT EXISTS reservations (
                                            reservation_id BIGSERIAL PRIMARY KEY,
                                            passenger_id BIGINT NOT NULL,
                                            status VARCHAR(250),
                                            created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                            trip_id BIGINT NOT NULL,
                                            CONSTRAINT fk_reservation_trip FOREIGN KEY (trip_id)
    REFERENCES trips (trip_id)
    ON DELETE RESTRICT
    );