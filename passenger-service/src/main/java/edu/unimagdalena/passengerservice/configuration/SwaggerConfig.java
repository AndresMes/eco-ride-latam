package edu.unimagdalena.passengerservice.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "ECO-RIDE-LATAM (Passenger Service)",
                description = "This is the API for PassengerService, which belongs to the app Eco-ride-latam®",
                version = "1.0.0"
        ),
        servers = {
                @Server(
                        description = "DEV ENV",
                        url = "http://localhost:8081"
                )
        }
)
public class SwaggerConfig {
}
