package com.adbrassacoma.administrativo.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("OpenApiConfig")
class OpenApiConfigTest {

    @Autowired
    private OpenApiConfig openApiConfig;

    @Test
    void customOpenAPIDeveRetornarConfiguracaoCompleta() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        assertNotNull(openAPI);

        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("Sistema de Controle Administrativo", info.getTitle());
        assertEquals("0.0.1-SNAPSHOT", info.getVersion());
        assertNotNull(info.getDescription());
        assertNotNull(info.getContact());
        assertEquals("dev@adbrassacoma.com", info.getContact().getEmail());
        assertNotNull(info.getLicense());

        List<Server> servers = openAPI.getServers();
        assertNotNull(servers);
        assertTrue(servers.size() >= 2);
        assertTrue(servers.stream().anyMatch(s -> s.getUrl().contains("localhost")));
        assertTrue(servers.stream().anyMatch(s -> s.getUrl().contains("producao")));

        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        assertTrue(openAPI.getComponents().getSecuritySchemes().containsKey("Bearer Authentication"));
    }
}
