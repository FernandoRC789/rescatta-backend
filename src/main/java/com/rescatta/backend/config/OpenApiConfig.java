package com.rescatta.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rescattaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Rescatta API")
                .version("v1")
                .description("API REST de reporte de mascotas callejeras y catálogo de adopción de Rescatta.")
                .contact(new Contact().name("Equipo Rescatta")));
    }
}
