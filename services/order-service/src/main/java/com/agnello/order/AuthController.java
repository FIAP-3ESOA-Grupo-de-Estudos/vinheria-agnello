package com.agnello.order;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Trades the customer's HTTP Basic credentials for a JWT, the only place a password is accepted. */
@RestController
@RequestMapping("/v1/auth")
class AuthController {

    private final TokenService tokens;

    AuthController(TokenService tokens) {
        this.tokens = tokens;
    }

    @PostMapping("/token")
    Map<String, Object> token(Authentication customer) {
        return Map.of(
                "access_token", tokens.customerToken(customer.getName()),
                "token_type", "Bearer",
                "expires_in", TokenService.CUSTOMER_TOKEN_TTL.toSeconds());
    }
}
