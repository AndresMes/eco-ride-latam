package edu.unimagdalena.tripservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TripMetricsConfig {

    private final Counter tripsCreatedCounter;
    private final Counter reservationsCreatedCounter;

    public TripMetricsConfig(MeterRegistry registry) {

        this.tripsCreatedCounter = Counter.builder("tripservice_trips_created_total")
                .description("Cantidad total de viajes creados")
                .register(registry);

        this.reservationsCreatedCounter = Counter.builder("tripservice_reservations_created_total")
                .description("Cantidad total de reservas creadas")
                .register(registry);
    }

    public void incrementTripsCreated() {
        tripsCreatedCounter.increment();
    }

    public void incrementReservationsCreated() {
        reservationsCreatedCounter.increment();
    }

}
