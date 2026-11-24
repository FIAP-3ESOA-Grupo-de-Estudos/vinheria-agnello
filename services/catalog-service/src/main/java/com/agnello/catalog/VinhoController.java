package com.agnello.catalog;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/vinhos")
class VinhoController {

    private final VinhoRepository repository;

    VinhoController(VinhoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    List<Vinho> listar() {
        return repository.findAll().stream().filter(Vinho::ativo).toList();
    }

    @GetMapping("/{id}")
    Vinho buscar(@PathVariable int id) {
        return repository.findById(id)
                .filter(Vinho::ativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
