package com.rescatta.backend.common.storage;

import com.rescatta.backend.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Guarda los archivos en el disco local, bajo {@code rescatta.storage.base-path}.
 * Los archivos quedan expuestos públicamente vía {@code WebMvcConfig} bajo
 * {@code rescatta.storage.public-url-prefix} (por defecto, "/uploads/**").
 *
 * En producción, esta clase se reemplaza por una implementación de Firebase Storage o S3
 * — el resto de la app no cambia una sola línea gracias a que depende de
 * {@link FileStorageService}, no de esta clase concreta.
 */
@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final StorageProperties storageProperties;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/heic", "image/webp");

    @Override
    public String store(MultipartFile file, String subfolder) {
        validateImage(file);
        try {
            Path targetDir = Path.of(storageProperties.basePath(), subfolder);
            Files.createDirectories(targetDir);

            String extension = extractExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;
            Path targetPath = targetDir.resolve(fileName);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "%s/%s/%s".formatted(storageProperties.publicUrlPrefix(), subfolder, fileName);
        } catch (IOException e) {
            throw new BadRequestException("No se pudo guardar el archivo: " + file.getOriginalFilename());
        }
    }

    @Override
    public List<String> storeAll(List<MultipartFile> files, String subfolder) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(store(file, subfolder));
        }
        return urls;
    }

    @Override
    public void delete(String publicUrl) {
        String relativePath = publicUrl.replaceFirst("^" + storageProperties.publicUrlPrefix() + "/", "");
        Path path = Path.of(storageProperties.basePath(), relativePath);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // No es crítico para el flujo principal: se registra pero no se interrumpe la operación.
            System.err.println("No se pudo eliminar el archivo: " + path);
        }
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Una de las fotos enviadas está vacía.");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Formato de imagen no soportado: " + file.getContentType());
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return ".jpg";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}
