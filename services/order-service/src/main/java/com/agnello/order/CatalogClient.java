package com.agnello.order;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** The Catalog Service boundary: where the Order Service asks for a wine's name and price. */
@Component
class CatalogClient {

    private final RestClient http;
    private final TokenService tokens;

    /**
     * @param trustBundle the SSL bundle that holds the CA the Catalog Service's certificate must chain to
     *     (profile "tls"); blank when the Catalog Service is reached over plain HTTP
     */
    CatalogClient(
            RestClient.Builder builder,
            @Value("${catalog.url}") String catalogUrl,
            @Value("${catalog.ssl-bundle:}") String trustBundle,
            ObjectProvider<RestClientSsl> ssl,
            TokenService tokens) {
        builder.baseUrl(catalogUrl);
        if (!trustBundle.isBlank()) {
            builder.apply(ssl.getObject().fromBundle(trustBundle));
        }
        this.http = builder.build();
        this.tokens = tokens;
    }

    /**
     * @throws CatalogUnavailableException when the Catalog Service cannot give the wine: it cannot be
     *     reached, it fails, it refuses the token, or it has no such active wine
     */
    CatalogVinho findVinho(int idVinho) {
        try {
            return http.get()
                    .uri("/v1/vinhos/{id}", idVinho)
                    // A fresh, one-minute token per call, issued for the Catalog Service only.
                    .headers(headers -> headers.setBearerAuth(tokens.catalogServiceToken()))
                    .retrieve()
                    .body(CatalogVinho.class);
        } catch (RestClientException failure) {
            throw new CatalogUnavailableException(failure);
        }
    }
}
