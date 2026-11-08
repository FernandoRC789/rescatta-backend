package com.rescatta.backend.config;

import com.rescatta.backend.common.storage.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final StorageProperties storageProperties;

    /**
     * Orígenes permitidos para CORS, ej. "https://admin.rescatta.app,http://localhost:5173".
     * Por defecto "*" (abierto) para no bloquear el desarrollo; en producción, configurar
     * la variable de entorno RESCATTA_CORS_ORIGINS con el dominio real del panel web.
     */
    @Value("${rescatta.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Expone ./uploads/** como http://host/uploads/** para que la app pueda cargar
        // directamente las fotos de reportes y mascotas sin pasar por un endpoint aparte.
        registry.addResourceHandler(storageProperties.publicUrlPrefix() + "/**")
                .addResourceLocations("file:" + storageProperties.basePath() + "/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(allowedOrigins.split(","))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
