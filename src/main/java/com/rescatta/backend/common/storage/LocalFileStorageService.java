package com.rescatta.backend.common.storage;

import com.rescatta.backend.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Guarda los archivos en el disco local, bajo {@code rescatta.storage.base-path}.
 * Los archivos quedan expuestos públicamente vía {@code WebMvcConfig} bajo
 * {@code rescatta.storage.public-url-prefix} (por defecto, "/uploads/**").
 *
 * <p>Esta implementación es solo para desarrollo local: en producción usa
 * {@code provider=r2} o {@code provider=cloudinary} para que las imágenes sobrevivan
 * a redeploys y no dependan del filesystem del contenedor.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rescatta.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    private final StorageProperties storageProperties;

    @Override
    public String store(MultipartFile file, String subfolder) {
        ImageValidator.validate(file);

        try {
            Path targetDir = Path.of(storageProperties.basePath(), subfolder);
            Files.createDirectories(targetDir);

            String extension = ImageValidator.extensionFor(file);
            String fileName = UUID.randomUUID() + extension;
            Path targetPath = targetDir.resolve(fileName);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return publicUrl(subfolder + "/" + fileName);
        } catch (IOException e) {
            throw new BadRequestException("No se pudo guardar el archivo: " + file.getOriginalFilename());
        }
    }

    @Override
    public void delete(String publicUrl) {
        String relativePath = relativePath(publicUrl);
        if (relativePath.isBlank()) {
            throw new BadRequestException("Ruta de archivo inválida.");
        }

        Path baseDir = Path.of(storageProperties.basePath()).toAbsolutePath().normalize();
        Path path = baseDir.resolve(relativePath).normalize();

        // Defensa contra path traversal: la ruta final debe seguir dentro de basePath.
        if (!path.startsWith(baseDir)) {
            throw new BadRequestException("Ruta de archivo inválida.");
        }

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // No es crítico para el flujo principal: se registra pero no se interrumpe la operación.
            System.err.println("No se pudo eliminar el archivo: " + path);
        }
    }

    private String publicUrl(String key) {
        String baseUrl = trimTrailingSlash(storageProperties.publicBaseUrl());
        String prefix = trimTrailingSlash(storageProperties.publicUrlPrefix());
        if (baseUrl == null || baseUrl.isBlank()) {
            return prefix + "/" + key;
        }
        return baseUrl + prefix + "/" + key;
    }

    private String relativePath(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) {
            throw new BadRequestException("URL de archivo inválida.");
        }

        String baseUrl = trimTrailingSlash(storageProperties.publicBaseUrl());
        String prefix = trimTrailingSlash(storageProperties.publicUrlPrefix());

        String withoutBase = publicUrl;
        if (baseUrl != null && !baseUrl.isBlank() && withoutBase.startsWith(baseUrl)) {
            withoutBase = withoutBase.substring(baseUrl.length());
        }

        // Si llega una URL absoluta (por ejemplo, datos viejos guardados con el host),
        // nos quedamos solo con el path para no intentar resolver "https://..." como archivo.
        if (withoutBase.contains("://")) {
            withoutBase = java.net.URI.create(withoutBase).getPath();
        }

        if (withoutBase == null) {
            throw new BadRequestException("URL de archivo inválida.");
        }

        if (prefix != null && !prefix.isBlank() && withoutBase.startsWith(prefix)) {
            withoutBase = withoutBase.substring(prefix.length());
        }

        return withoutBase.replaceFirst("^/+", "");
    }

    private String trimTrailingSlash(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }
}
