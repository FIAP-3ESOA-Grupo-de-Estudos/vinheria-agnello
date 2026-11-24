package com.agnello.catalog;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

/** Signs tokens the way the Order Service does, with the test key, so each test can bend one claim. */
final class TestTokens {

    static final String SECRET_PROPERTY = "agnello.jwt.secret=classpath:jwt-test.secret";

    private TestTokens() {
    }

    static SecretKey testKey() {
        try {
            return SecurityConfig.hmacKey(new ClassPathResource("jwt-test.secret"));
        } catch (IOException failure) {
            throw new IllegalStateException(failure);
        }
    }

    /** What the Order Service sends: issued for the Catalog Service, scope catalog:read, valid for a minute. */
    static String serviceToken() {
        return token(testKey(), SecurityConfig.ISSUER, SecurityConfig.AUDIENCE, "catalog:read", Duration.ofMinutes(1));
    }

    static String token(SecretKey key, String issuer, String audience, String scope, Duration validFor) {
        Instant issuedAt = validFor.isNegative() ? Instant.now().plus(validFor).minusSeconds(60) : Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject("order-service")
                .audience(List.of(audience))
                .issuedAt(issuedAt)
                .expiresAt(Instant.now().plus(validFor))
                .claim("scope", scope)
                .build();
        return new NimbusJwtEncoder(new ImmutableSecret<>(key))
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }

    static SecretKey otherKey() {
        return new SecretKeySpec("a-different-key-that-nobody-should-trust-00".getBytes(), "HmacSHA256");
    }
}
