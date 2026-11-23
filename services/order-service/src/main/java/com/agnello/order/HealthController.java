package com.agnello.order;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class HealthController {

    // Deliberately touches neither the database nor the Catalog Service: the Order Service's
    // health must not depend on its collaborators being up.
    @GetMapping("/health")
    Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
