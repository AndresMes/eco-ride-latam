package edu.unimagdalena.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * OAuth2 WebClient Configuration for Service-to-Service Communication
 *
 * Configures WebClient with OAuth2 support for:
 * - Client Credentials flow (eco-internal): Service-to-service authentication
 * - Authorization Code flow (eco-gateway): User token relay
 *
 * The configured WebClient automatically:
 * - Obtains access tokens using client_credentials grant
 * - Refreshes expired tokens
 * - Adds Authorization header to requests
 */
@Configuration
public class OAuth2ClientConfig {

    /**
     * Configure Reactive OAuth2 Authorized Client Manager
     *
     * Supports multiple OAuth2 grant types:
     * - authorization_code: User authentication flow
     * - client_credentials: Service-to-service authentication
     * - refresh_token: Token refresh for long-lived sessions
     */
    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {

        // Configure OAuth2 providers with support for multiple grant types
        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
                ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()      // For user authentication (eco-gateway)
                        .clientCredentials()       // For service-to-service (eco-internal)
                        .refreshToken()           // For token refresh
                        .build();

        // Create client manager with configured providers
        DefaultReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultReactiveOAuth2AuthorizedClientManager(
                        clientRegistrationRepository,
                        authorizedClientRepository
                );

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    /**
     * Configure WebClient with OAuth2 support for downstream service calls
     *
     * This WebClient automatically:
     * 1. Obtains access tokens using client_credentials (eco-internal)
     * 2. Adds Authorization: Bearer {token} header to requests
     * 3. Handles token refresh automatically
     *
     * Usage in services:
     * <pre>
     * webClient.get()
     *     .uri("http://trip-service/api/v1/trips")
     *     .attributes(clientRegistrationId("eco-internal"))
     *     .retrieve()
     *     .bodyToMono(TripResponse.class)
     * </pre>
     */
    @Bean
    public WebClient webClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        // Create OAuth2 filter function
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        // Set default client registration to eco-internal for service-to-service calls
        oauth2Filter.setDefaultClientRegistrationId("eco-internal");

        return WebClient.builder()
                .filter(oauth2Filter)
                .build();
    }
}