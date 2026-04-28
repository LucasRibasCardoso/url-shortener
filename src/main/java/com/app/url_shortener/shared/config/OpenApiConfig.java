package com.app.url_shortener.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenApi(
      @Value("${app.base-url:http://localhost:8080}") String baseUrl,
      @Value("${spring.application.name:url-shortener}") String applicationName) {

    return new OpenAPI()
        .info(
            new Info()
                .title("URL Shortener SaaS API")
                .description(
                    "API RESTful para encurtamento de URLs, redirecionamento de alta performance e futuro gerenciamento de métricas. Desenvolvida com Arquitetura Hexagonal e focada em escalabilidade.")
                .version("1.0.0")
                .contact(
                    new Contact()
                        .name("Lucas Ribas Cardoso")
                        .url("https://github.com/LucasRibasCardoso")
                        .email("lucas.rib.card@gmail.com"))
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
        .servers(
            List.of(
                new Server().url(baseUrl).description("Servidor Local (" + applicationName + ")")));
  }
}
