package com.agnello.order;

import java.math.BigDecimal;

/** The part of a wine that the Order Service reads from the Catalog Service. */
public record CatalogVinho(int idVinho, String nome, BigDecimal preco) {
}
