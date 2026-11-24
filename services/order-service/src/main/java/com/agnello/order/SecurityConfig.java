package com.agnello.order;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Two kinds of credential reach this service:
 * <ul>
 *   <li>HTTP Basic, only on {@code POST /v1/auth/token}: the demo customer trades a password for a JWT;</li>
 *   <li>that JWT (audience {@value TokenService#AUDIENCE_ORDER}, scope {@code carrinho:read}) on every Carrinho read.</li>
 * </ul>
 * The Order Service stands in for the architecture's Authentication Service: it issues the customer tokens and,
 * for its own calls to the Catalog Service, short-lived service tokens. Only {@code /health} is open.
 */
@Configuration
class SecurityConfig {

    static final String DEMO_USER = "cliente";

    @Bean
    SecurityFilterChain api(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // Spring's error page (a 404 for an unknown Carrinho) is rendered in an error dispatch.
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.GET, "/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/auth/token").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/v1/carrinhos/**").hasAuthority("SCOPE_carrinho:read")
                        .anyRequest().denyAll())
                .httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer(server -> server.jwt(Customizer.withDefaults()))
                // A stateless REST API authenticated by a header: no session, so no CSRF to protect.
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /** The demo customer. Its password is generated with the environment's secrets, never written in the repository. */
    @Bean
    UserDetailsService users(@Value("${agnello.demo.password}") Resource password, PasswordEncoder encoder) throws IOException {
        return new InMemoryUserDetailsManager(User.withUsername(DEMO_USER)
                .password(encoder.encode(read(password)))
                .roles("CLIENTE")
                .build());
    }

    @Bean
    SecretKey jwtKey(@Value("${agnello.jwt.secret}") Resource secret) throws IOException {
        byte[] key = Base64.getDecoder().decode(read(secret));
        if (key.length < 32) {
            throw new IllegalStateException("JWT key " + secret + " is shorter than 256 bits");
        }
        return new SecretKeySpec(key, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey jwtKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey));
    }

    /** Accepts only the customer tokens this service issued for itself: a token meant for the Catalog Service is refused. */
    @Bean
    JwtDecoder jwtDecoder(SecretKey jwtKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtKey).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(Duration.ofSeconds(5)),
                new JwtIssuerValidator(TokenService.ISSUER),
                new JwtClaimValidator<List<String>>(JwtClaimNames.AUD,
                        aud -> aud != null && aud.contains(TokenService.AUDIENCE_ORDER))));
        return decoder;
    }

    private static String read(Resource resource) throws IOException {
        try (InputStream in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8).trim();
        }
    }
}
