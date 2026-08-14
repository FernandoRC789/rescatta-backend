package com.rescatta.backend.config;

import com.rescatta.backend.common.storage.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final StorageProperties storageProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expone ./uploads/** como http://host/uploads/** para que la app pueda cargar
        // directamente las fotos de reportes y mascotas sin pasar por un endpoint aparte.
        registry.addResourceHandler(storageProperties.publicUrlPrefix() + "/**")
                .addResourceLocations("file:" + storageProperties.basePath() + "/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Abierto para desarrollo. En producción, restringir a los orígenes reales
        // (dominio del panel web de refugios, si existiera) — la app iOS nativa no
        // necesita CORS, esto es solo por si se prueba desde Swagger UI / Postman / web.
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
