package com.agnello.catalog;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import jakarta.servlet.DispatcherType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Every wine read needs a JWT signed with the environment's key, issued by the Order Service
 * for this service (audience {@value #AUDIENCE}) with the scope {@code catalog:read}, and not expired.
 * Only {@code /health} is open.
 */
@Configuration
class SecurityConfig {

    static final String ISSUER = "order.agnello.internal";
    static final String AUDIENCE = "catalog-service";

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // Spring's error page (a 404 for an unknown wine) is rendered in an error dispatch.
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.GET, "/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/vinhos/**").hasAuthority("SCOPE_catalog:read")
                        .anyRequest().denyAll())
                .oauth2ResourceServer(server -> server.jwt(Customizer.withDefaults()))
                // A stateless REST API authenticated by a header: no session, so no CSRF to protect.
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(@Value("${agnello.jwt.secret}") Resource secret) throws IOException {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(hmacKey(secret))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(Duration.ofSeconds(5)),
                new JwtIssuerValidator(ISSUER),
                // A token the Order Service issued to a customer (audience order-service) is refused here.
                new JwtClaimValidator<List<String>>(JwtClaimNames.AUD, aud -> aud != null && aud.contains(AUDIENCE))));
        return decoder;
    }

    /** The shared HS256 key: base64 text, at least 256 bits, kept outside the repository (see infra/security). */
    static SecretKey hmacKey(Resource secret) throws IOException {
        try (InputStream in = secret.getInputStream()) {
            byte[] key = Base64.getDecoder().decode(new String(in.readAllBytes(), StandardCharsets.US_ASCII).trim());
            if (key.length < 32) {
                throw new IllegalStateException("JWT key " + secret + " is shorter than 256 bits");
            }
            return new SecretKeySpec(key, "HmacSHA256");
        }
    }
}
