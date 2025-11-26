package edu.unimagdalena.apigateway.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Custom Authentication Success Handler for OAuth2 Login
 *
 * Redirects user back to the originally requested URL after successful authentication.
 * Uses the saved request URI from the security context.
 */
@Component
public class SavedRequestAwareAuthenticationSuccessHandler
        implements ServerAuthenticationSuccessHandler {

    private static final String REDIRECT_LOCATION_ATTRIBUTE =
            "SPRING_SECURITY_SAVED_REQUEST";

    @Override
    public Mono<Void> onAuthenticationSuccess(
            WebFilterExchange webFilterExchange,
            Authentication authentication) {

        ServerWebExchange exchange = webFilterExchange.getExchange();

        // Get the original requested URI from session
        return exchange.getSession()
                .flatMap(session -> {
                    // Retrieve saved request URI
                    String savedRequest = session.getAttribute(REDIRECT_LOCATION_ATTRIBUTE);

                    // Remove it from session
                    session.getAttributes().remove(REDIRECT_LOCATION_ATTRIBUTE);

                    // Determine redirect location
                    String redirectUrl = (savedRequest != null && !savedRequest.isEmpty())
                            ? savedRequest
                            : "/";

                    // Perform redirect
                    exchange.getResponse().setStatusCode(
                            org.springframework.http.HttpStatus.FOUND
                    );
                    exchange.getResponse().getHeaders().setLocation(
                            URI.create(redirectUrl)
                    );

                    return exchange.getResponse().setComplete();
                });
    }
}