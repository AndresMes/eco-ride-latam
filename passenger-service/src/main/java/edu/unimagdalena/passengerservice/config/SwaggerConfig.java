package edu.unimagdalena.passengerservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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

@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Introduce el token JWT como: `Bearer {token}`"
)
public class SwaggerConfig {
}
