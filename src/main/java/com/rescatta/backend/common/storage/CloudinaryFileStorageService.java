package com.rescatta.backend.common.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.rescatta.backend.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Guarda imágenes en Cloudinary y devuelve la URL segura (HTTPS) que entrega su CDN.
 *
 * <p>La URL se puede guardar tal cual en la base de datos. Para borrar, se deriva el
 * {@code public_id} desde la propia URL (Cloudinary incluye el public_id en el path).
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "rescatta.storage.provider", havingValue = "cloudinary")
public class CloudinaryFileStorageService implements FileStorageService {

    private static final Pattern VERSION_SEGMENT = Pattern.compile("(?:^|/)v\\d+/");

    private final Cloudinary cloudinary;
    private final StorageProperties storageProperties;

    @Override
    public String store(MultipartFile file, String subfolder) {
        ImageValidator.validate(file);

        String publicId = buildPublicId(subfolder);

        Map<String, Object> options = new LinkedHashMap<>();
        options.put("public_id", publicId);
        options.put("resource_type", "image");
        options.put("overwrite", false);

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);
            Object secureUrl = result.get("secure_url");
            if (secureUrl == null) {
                throw new BadRequestException("Cloudinary no devolvió una URL para la imagen.");
            }
            return secureUrl.toString();
        } catch (IOException e) {
            throw new BadRequestException("No se pudo subir la imagen a Cloudinary.");
        }
    }

    @Override
    public void delete(String publicUrl) {
        String publicId = publicIdFromUrl(publicUrl);
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "image")
            );
            Object status = result.get("result");
            if (status != null && "error".equals(status.toString())) {
                throw new BadRequestException("Cloudinary no pudo eliminar la imagen.");
            }
        } catch (IOException e) {
            throw new BadRequestException("No se pudo eliminar la imagen de Cloudinary.");
        }
    }

    private String buildPublicId(String subfolder) {
        StorageProperties.Cloudinary config = storageProperties.cloudinary();
        String root = config == null ? null : config.folder();
        String safeSubfolder = subfolder == null ? "general" : subfolder.trim()
                .replaceAll("[^a-zA-Z0-9/_-]", "")
                .replaceAll("^/+", "")
                .replaceAll("/+$", "");

        if (safeSubfolder.isBlank()) {
            safeSubfolder = "general";
        }

        String rootPrefix = root == null ? "" : root.trim()
                .replaceAll("[^a-zA-Z0-9/_-]", "")
                .replaceAll("^/+", "")
                .replaceAll("/+$", "");

        String folder = rootPrefix.isBlank() ? safeSubfolder : rootPrefix + "/" + safeSubfolder;
        return folder + "/" + UUID.randomUUID();
    }

    /**
     * Extrae el public_id de una URL de Cloudinary:
     * {@code https://res.cloudinary.com/&lt;cloud&gt;/image/upload/v123/&lt;folder&gt;/&lt;id&gt;.jpg}
     */
    private String publicIdFromUrl(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank()) {
            throw new BadRequestException("URL de archivo inválida.");
        }

        String path = URI.create(publicUrl).getPath();
        int uploadIndex = path.indexOf("/upload/");
        if (uploadIndex < 0) {
            throw new BadRequestException("La URL no parece ser de Cloudinary.");
        }

        String publicIdWithExtension = path.substring(uploadIndex + "/upload/".length());

        // Cloudinary puede insertar transformaciones antes de la versión, por ejemplo
        // /upload/c_fill,w_300/v123/folder/id.jpg. Nos quedamos con lo que sigue a /v123/.
        Matcher versionMatcher = VERSION_SEGMENT.matcher(publicIdWithExtension);
        if (versionMatcher.find()) {
            publicIdWithExtension = publicIdWithExtension.substring(versionMatcher.end());
        }

        int extensionIndex = publicIdWithExtension.lastIndexOf('.');
        if (extensionIndex > 0) {
            publicIdWithExtension = publicIdWithExtension.substring(0, extensionIndex);
        }

        return URLDecoder.decode(publicIdWithExtension, StandardCharsets.UTF_8);
    }
}
