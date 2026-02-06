package com.adbrassacoma.administrativo.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Sistema de Controle Administrativo")
						.version("0.0.1-SNAPSHOT")
						.description("API REST para o sistema de controle administrativo")
						.contact(new Contact()
								.name("Equipe de Desenvolvimento")
								.email("dev@adbrassacoma.com"))
						.license(new License()
								.name("Apache 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0.html")))
				.servers(List.of(
						new Server()
								.url("http://localhost:8080")
								.description("Servidor de Desenvolvimento"),
						new Server()
								.url("https://api.producao.com.br")
								.description("Servidor de Produção")))
				.components(new io.swagger.v3.oas.models.Components()
						.addSecuritySchemes("Bearer Authentication", new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.description("Insira o token JWT obtido no endpoint de login")));
	}
}
