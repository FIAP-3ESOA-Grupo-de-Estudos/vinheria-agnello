package com.agnello.catalog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/** Who may read the wine list: only the Order Service, with a fresh token issued for this service. */
@WebMvcTest
@Import({VinhoRepository.class, SecurityConfig.class})
@TestPropertySource(properties = TestTokens.SECRET_PROPERTY)
class CatalogSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void withoutATokenAnswersUnauthorized() throws Exception {
        mockMvc.perform(get("/v1/vinhos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aTokenThatIsNotAJwtAnswersUnauthorized() throws Exception {
        mockMvc.perform(get("/v1/vinhos").header("Authorization", "Bearer agnello-secret-token-2026"))
                .andExpect(status().isUnauthorized());
    }

    static Stream<Arguments> refusedTokens() {
        Duration minute = Duration.ofMinutes(1);
        return Stream.of(
                Arguments.of("signed with another key",
                        TestTokens.token(TestTokens.otherKey(), SecurityConfig.ISSUER, SecurityConfig.AUDIENCE, "catalog:read", minute)),
                Arguments.of("expired",
                        TestTokens.token(TestTokens.testKey(), SecurityConfig.ISSUER, SecurityConfig.AUDIENCE, "catalog:read", Duration.ofMinutes(-2))),
                Arguments.of("issued for the Order Service (a customer's token)",
                        TestTokens.token(TestTokens.testKey(), SecurityConfig.ISSUER, "order-service", "carrinho:read", minute)),
                Arguments.of("from another issuer",
                        TestTokens.token(TestTokens.testKey(), "evil.example", SecurityConfig.AUDIENCE, "catalog:read", minute)));
    }

    @ParameterizedTest(name = "a token {0} answers unauthorized")
    @MethodSource("refusedTokens")
    void refusesTheToken(String why, String token) throws Exception {
        mockMvc.perform(get("/v1/vinhos/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aValidTokenWithoutTheCatalogScopeAnswersForbidden() throws Exception {
        String token = TestTokens.token(TestTokens.testKey(), SecurityConfig.ISSUER, SecurityConfig.AUDIENCE, "carrinho:read", Duration.ofMinutes(1));
        mockMvc.perform(get("/v1/vinhos/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
