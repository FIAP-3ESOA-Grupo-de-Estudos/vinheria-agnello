package com.agnello.order;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/** Reads a token with the test key, checking the signature and expiry only, so a test can look at its claims. */
final class JwtTestDecoder {

    private JwtTestDecoder() {
    }

    static Jwt decode(String token) {
        return NimbusJwtDecoder.withSecretKey(testKey()).macAlgorithm(MacAlgorithm.HS256).build().decode(token);
    }

    static SecretKeySpec testKey() {
        try {
            String base64 = new String(new ClassPathResource("jwt-test.secret").getContentAsByteArray(), StandardCharsets.US_ASCII).trim();
            return new SecretKeySpec(Base64.getDecoder().decode(base64), "HmacSHA256");
        } catch (IOException failure) {
            throw new UncheckedIOException(failure);
        }
    }
}
