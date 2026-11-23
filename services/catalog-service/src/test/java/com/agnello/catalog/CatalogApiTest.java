package com.agnello.catalog;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/** The Catalog Service's own wine list: wines 1 to 4 are active, wine 5 is inactive. */
@WebMvcTest
@Import(VinhoRepository.class)
class CatalogApiTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void healthAnswersUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void getsAnExistingWineById() throws Exception {
        mockMvc.perform(get("/v1/vinhos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVinho").value(1))
                .andExpect(jsonPath("$.nome").value("Cabernet Sauvignon Reserva"))
                .andExpect(jsonPath("$.preco").value(89.90))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    void unknownWineIdAnswersNotFound() throws Exception {
        mockMvc.perform(get("/v1/vinhos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void inactiveWineAnswersNotFound() throws Exception {
        mockMvc.perform(get("/v1/vinhos/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listReturnsOnlyActiveWines() throws Exception {
        mockMvc.perform(get("/v1/vinhos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].idVinho", containsInAnyOrder(1, 2, 3, 4)));
    }
}
