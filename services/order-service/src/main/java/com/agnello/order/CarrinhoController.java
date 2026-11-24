package com.agnello.order;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/carrinhos")
class CarrinhoController {

    private final CarrinhoRepository repository;
    private final CatalogClient catalog;

    CarrinhoController(CarrinhoRepository repository, CatalogClient catalog) {
        this.repository = repository;
        this.catalog = catalog;
    }

    @GetMapping("/{id}")
    Carrinho buscar(@PathVariable int id) {
        List<CarrinhoRepository.Item> saved = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return new Carrinho(id, saved.stream().map(this::withNameAndPrice).toList());
    }

    /** The name and price come from the Catalog Service, by DNS name, on every read. */
    private ItemCarrinho withNameAndPrice(CarrinhoRepository.Item item) {
        CatalogVinho vinho = catalog.findVinho(item.idVinho());
        return new ItemCarrinho(vinho.idVinho(), vinho.nome(), item.quantidade(), vinho.preco());
    }
}
