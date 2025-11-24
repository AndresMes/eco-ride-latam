package edu.unimagdalena.tripservice.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "ECO-RIDE-LATAM (Trip Service)",
                description = "This is the API for TripService, which belongs to the app Eco-ride-latam®",
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
