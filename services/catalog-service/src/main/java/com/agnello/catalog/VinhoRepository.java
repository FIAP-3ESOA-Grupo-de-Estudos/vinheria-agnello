package com.agnello.catalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** The Catalog Service's own wine data, kept in memory and seeded at startup. Wine 5 is inactive. */
@Component
class VinhoRepository {

    private final List<Vinho> vinhos = List.of(
            new Vinho(1, "Cabernet Sauvignon Reserva", "tinto", "Vale dos Vinhedos", 2020, new BigDecimal("89.90"), true),
            new Vinho(2, "Merlot Especial", "tinto", "Serra Gaúcha", 2021, new BigDecimal("69.90"), true),
            new Vinho(3, "Malbec Andes", "tinto", "Mendoza", 2019, new BigDecimal("109.90"), true),
            new Vinho(4, "Chardonnay Premium", "branco", "Serra Gaúcha", 2022, new BigDecimal("79.90"), true),
            new Vinho(5, "Merlot Descontinuado", "tinto", "Serra Gaúcha", 2015, new BigDecimal("59.90"), false));

    List<Vinho> findAll() {
        return vinhos;
    }

    Optional<Vinho> findById(int idVinho) {
        return vinhos.stream().filter(vinho -> vinho.idVinho() == idVinho).findFirst();
    }
}
