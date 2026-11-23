package com.agnello.order;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * The Order Service's own Carrinhos, kept in memory and seeded at startup. A Carrinho holds only
 * wine ids and quantities: names and prices belong to the Catalog Service.
 */
@Component
class CarrinhoRepository {

    record Item(int idVinho, int quantidade) {
    }

    private final Map<Integer, List<Item>> carrinhos = Map.of(
            1, List.of(new Item(1, 2), new Item(4, 1)),
            2, List.of(new Item(2, 1), new Item(3, 1)));

    Optional<List<Item>> findById(int idCarrinho) {
        return Optional.ofNullable(carrinhos.get(idCarrinho));
    }
}
