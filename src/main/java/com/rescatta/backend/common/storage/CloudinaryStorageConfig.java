package com.rescatta.backend.common.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cliente de Cloudinary para subir y borrar imágenes.
 *
 * <p>Cloudinary tiene un free tier que no requiere tarjeta y devuelve URLs HTTPS
 * servidas por su CDN, además de permitir transformaciones (thumbnails, WebP, etc.).
 */
@Configuration
@ConditionalOnProperty(name = "rescatta.storage.provider", havingValue = "cloudinary")
public class CloudinaryStorageConfig {

    @Bean
    public Cloudinary cloudinary(StorageProperties properties) {
        StorageProperties.Cloudinary cloudinary = properties.cloudinary();
        if (cloudinary == null) {
            throw new IllegalStateException("Falta la configuración rescatta.storage.cloudinary.");
        }

        require(cloudinary.cloudName(), "CLOUDINARY_CLOUD_NAME");
        require(cloudinary.apiKey(), "CLOUDINARY_API_KEY");
        require(cloudinary.apiSecret(), "CLOUDINARY_API_SECRET");

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudinary.cloudName(),
                "api_key", cloudinary.apiKey(),
                "api_secret", cloudinary.apiSecret(),
                "secure", true
        ));
    }

    private void require(String value, String envName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta la variable de entorno " + envName + " para Cloudinary.");
        }
    }
}
