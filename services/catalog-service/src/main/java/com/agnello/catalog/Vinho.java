package com.agnello.catalog;

import java.math.BigDecimal;

/** A wine, with the field names of the monolith's Vinho entity. */
public record Vinho(
        int idVinho,
        String nome,
        String tipo,
        String regiao,
        int safra,
        BigDecimal preco,
        boolean ativo) {
}
