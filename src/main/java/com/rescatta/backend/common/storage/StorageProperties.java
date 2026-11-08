package com.rescatta.backend.common.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Mapea la sección {@code rescatta.storage} de application.yml a un objeto tipado.
 *
 * <p>El proveedor se elige con {@code rescatta.storage.provider}:
 * <ul>
 *   <li>{@code local} — disco local, solo para desarrollo.</li>
 *   <li>{@code r2} — Cloudflare R2 (S3-compatible).</li>
 *   <li>{@code cloudinary} — Cloudinary (free tier, sin tarjeta).</li>
 * </ul>
 */
@ConfigurationProperties(prefix = "rescatta.storage")
public record StorageProperties(
        String provider,
        String basePath,
        String publicUrlPrefix,
        String publicBaseUrl,
        R2 r2,
        Cloudinary cloudinary
) {

    /** Configuración de Cloudflare R2. */
    public record R2(
            String endpoint,
            String bucket,
            String accessKey,
            String secretKey,
            String region
    ) {
    }

    /** Configuración de Cloudinary. */
    public record Cloudinary(
            String cloudName,
            String apiKey,
            String apiSecret,
            String folder
    ) {
    }
}
