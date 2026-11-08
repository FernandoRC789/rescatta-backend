package com.rescatta.backend.common.storage;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Falla rápido al arrancar si el proveedor de storage no es válido o le falta
 * configuración. Es mejor que arrancar “bien” y descubrir en el primer reporte que
 * las imágenes no se pueden guardar.
 */
@Component
@RequiredArgsConstructor
public class StorageProviderValidator {

    private final StorageProperties properties;

    @PostConstruct
    void validate() {
        String provider = properties.provider();
        if (provider == null || provider.isBlank()) {
            throw new IllegalStateException(
                    "Configura rescatta.storage.provider con local, r2 o cloudinary.");
        }

        switch (provider) {
            case "local" -> {
                require(properties.basePath(), "rescatta.storage.base-path");
                require(properties.publicUrlPrefix(), "rescatta.storage.public-url-prefix");
            }
            case "r2" -> {
                require(properties.publicBaseUrl(), "STORAGE_PUBLIC_BASE_URL");
                StorageProperties.R2 r2 = properties.r2();
                if (r2 == null) {
                    throw new IllegalStateException("Falta la configuración rescatta.storage.r2.");
                }
                require(r2.endpoint(), "R2_ENDPOINT");
                require(r2.bucket(), "R2_BUCKET");
                require(r2.accessKey(), "R2_ACCESS_KEY");
                require(r2.secretKey(), "R2_SECRET_KEY");
            }
            case "cloudinary" -> {
                StorageProperties.Cloudinary cloudinary = properties.cloudinary();
                if (cloudinary == null) {
                    throw new IllegalStateException("Falta la configuración rescatta.storage.cloudinary.");
                }
                require(cloudinary.cloudName(), "CLOUDINARY_CLOUD_NAME");
                require(cloudinary.apiKey(), "CLOUDINARY_API_KEY");
                require(cloudinary.apiSecret(), "CLOUDINARY_API_SECRET");
            }
            default -> throw new IllegalStateException(
                    "Proveedor de storage no soportado: " + provider + ". Usa local, r2 o cloudinary.");
        }
    }

    private void require(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta la configuración " + name + ".");
        }
    }
}
