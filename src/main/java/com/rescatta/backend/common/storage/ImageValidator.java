package com.rescatta.backend.common.storage;

import com.rescatta.backend.common.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Validación de imágenes por contenido real (magic bytes), no solo por el
 * {@code Content-Type} que envía el cliente.
 *
 * <p>Esto evita que alguien suba un HTML/JS disfrazado de imagen y luego se sirva
 * públicamente desde {@code /uploads/**} como si fuera una página.
 */
public final class ImageValidator {

    private static final int HEADER_BYTES = 16;

    private ImageValidator() {
    }

    /**
     * Valida que el archivo sea una imagen soportada y que su contenido coincida con un
     * formato conocido. Lanza {@link BadRequestException} con un mensaje apto para el cliente.
     */
    public static void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Una de las fotos enviadas está vacía.");
        }

        String declaredType = file.getContentType();
        if (declaredType != null && !declaredType.isBlank()) {
            String normalized = declaredType.toLowerCase(Locale.ROOT);
            boolean looksLikeImage = normalized.startsWith("image/")
                    || normalized.equals("application/octet-stream");
            if (!looksLikeImage) {
                throw new BadRequestException("Formato de imagen no soportado: " + declaredType);
            }
        }

        if (detectFormat(file) == null) {
            throw new BadRequestException("El archivo no parece ser una imagen válida (JPEG, PNG, WebP o HEIC).");
        }
    }

    /** Extensión segura derivada del contenido real, nunca del nombre original. */
    public static String extensionFor(MultipartFile file) {
        return switch (detectFormat(file)) {
            case "jpeg" -> ".jpg";
            case "png" -> ".png";
            case "webp" -> ".webp";
            case "heic" -> ".heic";
            case "heif" -> ".heif";
            default -> throw new BadRequestException("Formato de imagen no soportado.");
        };
    }

    /** MIME real derivado del contenido, para enviarlo al storage. */
    public static String contentTypeFor(MultipartFile file) {
        return switch (detectFormat(file)) {
            case "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            case "heic" -> "image/heic";
            case "heif" -> "image/heif";
            default -> "application/octet-stream";
        };
    }

    private static String detectFormat(MultipartFile file) {
        byte[] header = new byte[HEADER_BYTES];
        try (InputStream in = file.getInputStream()) {
            int read = in.read(header);
            if (read < 12) {
                return null;
            }

            // JPEG: FF D8 FF
            if ((header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF) {
                return "jpeg";
            }

            // PNG: 89 50 4E 47
            if ((header[0] & 0xFF) == 0x89
                    && header[1] == 'P' && header[2] == 'N' && header[3] == 'G') {
                return "png";
            }

            // WebP: RIFF....WEBP
            if (header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                    && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
                return "webp";
            }

            // HEIC/HEIF: ISO BMFF, bytes 4-7 = "ftyp", marca en bytes 8-11
            if (header[4] == 'f' && header[5] == 't' && header[6] == 'y' && header[7] == 'p') {
                String brand = new String(header, 8, 4, StandardCharsets.US_ASCII)
                        .toLowerCase(Locale.ROOT);
                if (brand.startsWith("heic") || brand.startsWith("heix")
                        || brand.startsWith("hevc") || brand.startsWith("hevx")) {
                    return "heic";
                }
                if (brand.startsWith("mif1") || brand.startsWith("msf1")) {
                    return "heif";
                }
            }

            return null;
        } catch (IOException e) {
            throw new BadRequestException("No se pudo leer la imagen enviada.");
        }
    }
}
