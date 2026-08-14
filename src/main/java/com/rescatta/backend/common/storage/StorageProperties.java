package com.rescatta.backend.common.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Mapea la sección {@code rescatta.storage} de application.yml a un objeto tipado,
 * en vez de esparcir {@code @Value("${rescatta.storage.base-path}")} por varias clases.
 */
@ConfigurationProperties(prefix = "rescatta.storage")
public record StorageProperties(String basePath, String publicUrlPrefix) {
}
