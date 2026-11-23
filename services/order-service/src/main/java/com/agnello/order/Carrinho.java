package com.agnello.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public record Carrinho(int idCarrinho, List<ItemCarrinho> itens) {

    @JsonProperty("total")
    public BigDecimal total() {
        return itens.stream()
                .map(item -> item.precoUnitario().multiply(BigDecimal.valueOf(item.quantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
