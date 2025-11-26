package edu.unimagdalena.paymentservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "ECO-RIDE-LATAM (Payment Service)",
                description = "Payment Service with SAGA pattern for Eco-Ride LATAM platform",
                version = "1.0.0"
        ),
        servers = {
                @Server(
                        description = "DEV ENV",
                        url = "http://localhost:8083"
                )
        }
)
public class SwaggerConfig {
}
