package com.agnello.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/** The Catalog Service boundary: where the Order Service asks for a wine's name and price. */
@Component
class CatalogClient {

    private final RestClient http;

    CatalogClient(RestClient.Builder builder, @Value("${catalog.url}") String catalogUrl) {
        this.http = builder.baseUrl(catalogUrl).build();
    }

    /**
     * @throws CatalogUnavailableException when the Catalog Service cannot give the wine: it cannot be
     *     reached, it fails, or it has no such active wine
     */
    CatalogVinho findVinho(int idVinho) {
        try {
            return http.get()
                    .uri("/v1/vinhos/{id}", idVinho)
                    .retrieve()
                    .body(CatalogVinho.class);
        } catch (RestClientException failure) {
            throw new CatalogUnavailableException(failure);
        }
    }
}
