package com.agnello.order;

import java.math.BigDecimal;

/** One wine in a Carrinho, with its name and unit price as they were when it was added. */
public record ItemCarrinho(int idVinho, String nomeVinho, int quantidade, BigDecimal precoUnitario) {
}
