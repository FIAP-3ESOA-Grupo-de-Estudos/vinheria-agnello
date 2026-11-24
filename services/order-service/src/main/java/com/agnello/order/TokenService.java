package com.agnello.order;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

/** Issues the environment's JWTs (HS256). Each token names one audience, so it opens one service only. */
@Component
class TokenService {

    static final String ISSUER = "order.agnello.internal";
    static final String AUDIENCE_ORDER = "order-service";
    static final String AUDIENCE_CATALOG = "catalog-service";
    /** A customer's session: long enough to browse, short enough that a leaked token soon stops working. */
    static final Duration CUSTOMER_TOKEN_TTL = Duration.ofMinutes(15);
    /** A call to the Catalog Service takes milliseconds; a sniffed service token is useful for one minute at most. */
    static final Duration SERVICE_TOKEN_TTL = Duration.ofSeconds(60);

    private final JwtEncoder encoder;

    TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    String customerToken(String username) {
        return issue(username, AUDIENCE_ORDER, "carrinho:read", CUSTOMER_TOKEN_TTL);
    }

    /** The token the Order Service shows the Catalog Service on every wine read. */
    String catalogServiceToken() {
        return issue("order-service", AUDIENCE_CATALOG, "catalog:read", SERVICE_TOKEN_TTL);
    }

    private String issue(String subject, String audience, String scope, Duration ttl) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject(subject)
                .audience(List.of(audience))
                .issuedAt(now)
                .expiresAt(now.plus(ttl))
                .id(UUID.randomUUID().toString())
                .claim("scope", scope)
                .build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }
}
