package com.agnello.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.ConnectException;
import java.time.Duration;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.RestClient;

/** The Order Service's own Carrinhos: Carrinho 1 holds wine 1 x2 and wine 4 x1, Carrinho 2 holds wines 2 and 3. */
@WebMvcTest
@Import({CarrinhoRepository.class, CatalogClient.class, TokenService.class, SecurityConfig.class, OrderApiTest.CatalogHttpFake.class})
@TestPropertySource(properties = {
        "catalog.url=" + OrderApiTest.CATALOG_URL,
        "agnello.jwt.secret=classpath:jwt-test.secret",
        "agnello.demo.password=classpath:demo-password-test"})
class OrderApiTest {

    static final String CATALOG_URL = "http://catalog.test:8081";

    /** The Catalog Service, faked at the HTTP boundary: the real client talks to this instead of the network. */
    @TestConfiguration
    static class CatalogHttpFake {

        private final RestClient.Builder clientBuilder = RestClient.builder();
        private final MockRestServiceServer catalogService = MockRestServiceServer.bindTo(clientBuilder).build();

        @Bean
        RestClient.Builder restClientBuilder() {
            return clientBuilder;
        }

        @Bean
        MockRestServiceServer catalogService() {
            return catalogService;
        }
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MockRestServiceServer catalogService;

    @Autowired
    TokenService tokens;

    @BeforeEach
    void forgetEarlierCatalogExpectations() {
        catalogService.reset();
    }

    /** A Carrinho read as a logged-in customer sends it: with the token from POST /v1/auth/token. */
    private MockHttpServletRequestBuilder customerGet(String path) {
        return get(path).header("Authorization", "Bearer " + tokens.customerToken("cliente"));
    }

    @Test
    void healthAnswersUpWithoutAToken() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void getsACarrinhoWithTheNamesAndPricesTheCatalogServiceGives() throws Exception {
        catalogAnswersWithWine(1, "Cabernet Sauvignon Reserva", "89.90");
        catalogAnswersWithWine(4, "Chardonnay Premium", "79.90");

        // 2 x 89.90 + 1 x 79.90 = 259.70
        mockMvc.perform(customerGet("/v1/carrinhos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCarrinho").value(1))
                .andExpect(jsonPath("$.itens", hasSize(2)))
                .andExpect(jsonPath("$.itens[0].idVinho").value(1))
                .andExpect(jsonPath("$.itens[0].nomeVinho").value("Cabernet Sauvignon Reserva"))
                .andExpect(jsonPath("$.itens[0].quantidade").value(2))
                .andExpect(jsonPath("$.itens[0].precoUnitario").value(89.90))
                .andExpect(jsonPath("$.itens[1].idVinho").value(4))
                .andExpect(jsonPath("$.total").value(259.70));
        catalogService.verify();
    }

    @Test
    void unknownCarrinhoAnswersNotFoundWithoutAskingTheCatalogService() throws Exception {
        mockMvc.perform(customerGet("/v1/carrinhos/999"))
                .andExpect(status().isNotFound());
        catalogService.verify();
    }

    static Stream<ResponseCreator> catalogServiceFailures() {
        return Stream.of(
                withException(new ConnectException("Connection refused")),
                withServerError(),
                withStatus(NOT_FOUND));
    }

    /** Unreachable, failing, or without the wine: in every case the Carrinho cannot be completed. */
    @ParameterizedTest
    @MethodSource("catalogServiceFailures")
    void aCatalogServiceFailureAnswersServiceUnavailable(ResponseCreator catalogAnswer) throws Exception {
        catalogRequestFor(1).andRespond(catalogAnswer);

        mockMvc.perform(customerGet("/v1/carrinhos/1"))
                .andExpect(status().isServiceUnavailable());
    }

    /** Every call carries a service token: issued for the Catalog Service, scope catalog:read, valid for a minute. */
    private ResponseActions catalogRequestFor(int idVinho) {
        return catalogService.expect(requestTo(CATALOG_URL + "/v1/vinhos/" + idVinho))
                .andExpect(method(GET))
                .andExpect(request -> {
                    String header = request.getHeaders().getFirst("Authorization");
                    assertThat(header).startsWith("Bearer ");
                    Jwt token = JwtTestDecoder.decode(header.substring("Bearer ".length()));
                    assertThat(token.getAudience()).containsExactly(TokenService.AUDIENCE_CATALOG);
                    assertThat(token.getClaimAsString("scope")).isEqualTo("catalog:read");
                    assertThat(token.getSubject()).isEqualTo("order-service");
                    assertThat(Duration.between(token.getIssuedAt(), token.getExpiresAt())).isEqualTo(Duration.ofSeconds(60));
                });
    }

    /** The Catalog Service answers the wine with the fields it really sends: more than the Order Service reads. */
    private void catalogAnswersWithWine(int idVinho, String nome, String preco) {
        catalogRequestFor(idVinho)
                .andRespond(withSuccess(
                        "{\"idVinho\":" + idVinho + ",\"nome\":\"" + nome + "\",\"preco\":" + preco
                                + ",\"tipo\":\"tinto\",\"safra\":2020,\"ativo\":true}",
                        APPLICATION_JSON));
    }
}
