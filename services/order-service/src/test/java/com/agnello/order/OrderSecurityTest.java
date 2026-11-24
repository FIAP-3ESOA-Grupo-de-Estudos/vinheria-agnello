package com.agnello.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/** Logging in (Basic for a JWT) and who may read a Carrinho: only a customer, with a token issued for this service. */
@WebMvcTest
@Import({CarrinhoRepository.class, CatalogClient.class, TokenService.class, SecurityConfig.class, OrderApiTest.CatalogHttpFake.class})
@TestPropertySource(properties = {
        "catalog.url=" + OrderApiTest.CATALOG_URL,
        "agnello.jwt.secret=classpath:jwt-test.secret",
        "agnello.demo.password=classpath:demo-password-test"})
class OrderSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TokenService tokens;

    @Test
    void theCustomerTradesItsPasswordForAFifteenMinuteToken() throws Exception {
        String body = mockMvc.perform(post("/v1/auth/token").with(httpBasic("cliente", "test-only-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(900))
                .andReturn().getResponse().getContentAsString();

        Jwt token = JwtTestDecoder.decode(new ObjectMapper().readTree(body).get("access_token").asText());
        assertThat(token.getSubject()).isEqualTo("cliente");
        assertThat(token.getClaimAsString("iss")).isEqualTo(TokenService.ISSUER);
        assertThat(token.getAudience()).containsExactly(TokenService.AUDIENCE_ORDER);
        assertThat(token.getClaimAsString("scope")).isEqualTo("carrinho:read");
        assertThat(Duration.between(token.getIssuedAt(), token.getExpiresAt())).isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    void aWrongPasswordAnswersUnauthorized() throws Exception {
        mockMvc.perform(post("/v1/auth/token").with(httpBasic("cliente", "agnello")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loggingInWithoutCredentialsAnswersUnauthorized() throws Exception {
        mockMvc.perform(post("/v1/auth/token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aCarrinhoWithoutATokenAnswersUnauthorized() throws Exception {
        mockMvc.perform(get("/v1/carrinhos/1"))
                .andExpect(status().isUnauthorized());
    }

    /** The password opens the login endpoint only: every other call needs the token. */
    @Test
    void aCarrinhoReadWithThePasswordAnswersForbidden() throws Exception {
        mockMvc.perform(get("/v1/carrinhos/1").with(httpBasic("cliente", "test-only-password")))
                .andExpect(status().isForbidden());
    }

    /** A sniffed service token opens the Catalog Service only, not the Order Service. */
    @Test
    void aTokenIssuedForTheCatalogServiceAnswersUnauthorized() throws Exception {
        mockMvc.perform(get("/v1/carrinhos/1").header("Authorization", "Bearer " + tokens.catalogServiceToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anExpiredCustomerTokenAnswersUnauthorized() throws Exception {
        Instant twoMinutesAgo = Instant.now().minusSeconds(120);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(TokenService.ISSUER)
                .subject("cliente")
                .audience(List.of(TokenService.AUDIENCE_ORDER))
                .issuedAt(twoMinutesAgo.minusSeconds(900))
                .expiresAt(twoMinutesAgo)
                .claim("scope", "carrinho:read")
                .build();
        String expired = new NimbusJwtEncoder(new ImmutableSecret<>(JwtTestDecoder.testKey()))
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();

        mockMvc.perform(get("/v1/carrinhos/1").header("Authorization", "Bearer " + expired))
                .andExpect(status().isUnauthorized());
    }
}
