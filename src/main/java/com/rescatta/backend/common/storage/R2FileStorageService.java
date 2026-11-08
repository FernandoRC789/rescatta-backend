package com.rescatta.backend.common.storage;

import com.rescatta.backend.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

/**
 * Guarda imágenes en un bucket de Cloudflare R2 y devuelve su URL pública.
 *
 * <p>Requisitos en Cloudflare:
 * <ul>
 *   <li>Bucket creado.</li>
 *   <li>Bucket con acceso público (r2.dev) o un dominio custom conectado.</li>
 *   <li>API Token con permiso de escritura/borrado sobre el bucket.</li>
 *   <li>{@code STORAGE_PUBLIC_BASE_URL} apuntando a la URL pública del bucket.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rescatta.storage.provider", havingValue = "r2")
public class R2FileStorageService implements FileStorageService {

    private final S3Client s3Client;
    private final StorageProperties storageProperties;

    @Override
    public String store(MultipartFile file, String subfolder) {
        ImageValidator.validate(file);

        String key = buildKey(subfolder, ImageValidator.extensionFor(file));
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket())
                .key(key)
                .contentType(ImageValidator.contentTypeFor(file))
                .cacheControl("public, max-age=31536000, immutable")
                .build();

        try (InputStream in = file.getInputStream()) {
            s3Client.putObject(request, RequestBody.fromInputStream(in, file.getSize()));
        } catch (IOException e) {
            throw new BadRequestException("No se pudo leer la imagen: " + file.getOriginalFilename());
        } catch (SdkException e) {
            throw new BadRequestException("No se pudo subir la imagen a Cloudflare R2.");
        }

        return publicUrl(key);
    }

    @Override
    public void delete(String publicUrl) {
        String key = keyFromUrl(publicUrl);
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket())
                    .key(key)
                    .build());
        } catch (SdkException e) {
            throw new BadRequestException("No se pudo eliminar la imagen de Cloudflare R2.");
        }
    }

    private String buildKey(String subfolder, String extension) {
        String safeSubfolder = subfolder == null ? "general" : subfolder.trim()
                .replaceAll("[^a-zA-Z0-9/_-]", "")
                .replaceAll("^/+", "")
                .replaceAll("/+$", "");
        if (safeSubfolder.isBlank()) {
            safeSubfolder = "general";
        }
        return safeSubfolder + "/" + UUID.randomUUID() + extension;
    }

    private String publicUrl(String key) {
        String baseUrl = trimTrailingSlash(storageProperties.publicBaseUrl());
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException(
                    "Falta STORAGE_PUBLIC_BASE_URL: la URL pública del bucket R2 (r2.dev o dominio custom).");
        }
        return baseUrl + "/" + key;
    }

    private String keyFromUrl(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) {
            throw new BadRequestException("URL de archivo inválida.");
        }

        String baseUrl = trimTrailingSlash(storageProperties.publicBaseUrl());
        if (baseUrl != null && !baseUrl.isBlank() && publicUrl.startsWith(baseUrl + "/")) {
            return publicUrl.substring(baseUrl.length() + 1);
        }

        // Fallback: si cambió el dominio público, intentamos recuperar el path.
        String path = URI.create(publicUrl).getPath();
        return path == null ? publicUrl : path.replaceFirst("^/+", "");
    }

    private String bucket() {
        return storageProperties.r2().bucket();
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
