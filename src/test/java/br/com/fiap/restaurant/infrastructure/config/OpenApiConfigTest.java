package br.com.fiap.restaurant.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OpenApiConfigTest {
    @Test void publishesVersionedApiAndBearerSecurityScheme() {
        var api = new OpenApiConfig().restaurantOpenApi();
        assertEquals("Restaurant API", api.getInfo().getTitle());
        assertEquals("v1", api.getInfo().getVersion());
        assertEquals("/api/v1", api.getServers().getFirst().getUrl());
        assertEquals("bearer", api.getComponents().getSecuritySchemes().get("bearerAuth").getScheme());
        assertEquals("JWT", api.getComponents().getSecuritySchemes().get("bearerAuth").getBearerFormat());
    }
}
