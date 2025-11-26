-- Tabla passengers
CREATE TABLE IF NOT EXISTS passengers (
                                          passenger_id BIGSERIAL PRIMARY KEY,
                                          keycloak_sub VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    rating_avg DOUBLE PRECISION,
    created_at TIMESTAMP
    );



-- Tabla driver_profiles
CREATE TABLE IF NOT EXISTS driver_profiles (
    driver_id BIGSERIAL PRIMARY KEY,
    license_no VARCHAR(255) NOT NULL UNIQUE,
    car_plate VARCHAR(255) NOT NULL,
    verification_status VARCHAR(50),
    passenger_id BIGINT NOT NULL,
    CONSTRAINT fk_driver_passenger FOREIGN KEY (passenger_id)
    REFERENCES passengers(passenger_id) ON DELETE CASCADE,
    CONSTRAINT uq_driver_passenger UNIQUE (passenger_id)
    );


-- Tabla rating
CREATE TABLE IF NOT EXISTS rating (
    rating_id BIGSERIAL PRIMARY KEY,
    score INTEGER NOT NULL,
    comment VARCHAR(500),
    trip_id BIGINT NOT NULL,
    from_id BIGINT NOT NULL,
    to_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_rating_from FOREIGN KEY (from_id)
    REFERENCES passengers(passenger_id) ON DELETE CASCADE,
    CONSTRAINT fk_rating_to FOREIGN KEY (to_id)
    REFERENCES driver_profiles(driver_id) ON DELETE CASCADE,
    CONSTRAINT chk_score CHECK (score >= 1 AND score <= 5)
    );