package com.agnello.order;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

/** The Order Service's own Carrinhos: Carrinho 1 holds wine 1 x2 and wine 4 x1, Carrinho 2 holds wines 2 and 3. */
@WebMvcTest
@Import({CarrinhoRepository.class, CatalogClient.class, OrderApiTest.CatalogHttpFake.class})
@TestPropertySource(properties = "catalog.url=" + OrderApiTest.CATALOG_URL)
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

    @BeforeEach
    void forgetEarlierCatalogExpectations() {
        catalogService.reset();
    }

    @Test
    void healthAnswersUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void getsACarrinhoWithTheNamesAndPricesTheCatalogServiceGives() throws Exception {
        catalogAnswersWithWine(1, "Cabernet Sauvignon Reserva", "89.90");
        catalogAnswersWithWine(4, "Chardonnay Premium", "79.90");

        // 2 x 89.90 + 1 x 79.90 = 259.70
        mockMvc.perform(get("/v1/carrinhos/1"))
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
        mockMvc.perform(get("/v1/carrinhos/999"))
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

        mockMvc.perform(get("/v1/carrinhos/1"))
                .andExpect(status().isServiceUnavailable());
    }

    private ResponseActions catalogRequestFor(int idVinho) {
        return catalogService.expect(requestTo(CATALOG_URL + "/v1/vinhos/" + idVinho))
                .andExpect(method(GET));
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
