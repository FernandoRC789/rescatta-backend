package com.rescatta.backend.common.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstracción de almacenamiento de archivos.
 *
 * <p>Implementaciones disponibles:
 * <ul>
 *   <li>{@link LocalFileStorageService} — disco local ({@code rescatta.storage.provider=local}).</li>
 *   <li>{@link R2FileStorageService} — Cloudflare R2 ({@code rescatta.storage.provider=r2}).</li>
 *   <li>{@link CloudinaryFileStorageService} — Cloudinary ({@code rescatta.storage.provider=cloudinary}).</li>
 * </ul>
 *
 * <p>Los controllers y services dependen únicamente de esta interfaz, así que cambiar de
 * proveedor es solo configuración + variables de entorno.
 */
public interface FileStorageService {

    /**
     * Guarda un único archivo dentro de la subcarpeta indicada y devuelve la URL pública
     * con la que el cliente podrá acceder a él.
     */
    String store(MultipartFile file, String subfolder);

    /**
     * Guarda varios archivos y devuelve sus URLs públicas, en el mismo orden recibido.
     *
     * <p>Si uno falla, elimina los que ya se habían guardado para no dejar huérfanos.
     * No todas las implementaciones necesitan sobrescribirlo.
     */
    default List<String> storeAll(List<MultipartFile> files, String subfolder) {
        List<String> urls = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                urls.add(store(file, subfolder));
            }
            return urls;
        } catch (RuntimeException ex) {
            urls.forEach(this::deleteQuietly);
            throw ex;
        }
    }

    /** Elimina un archivo previamente almacenado, dada su URL pública. */
    void delete(String publicUrl);

    /** Igual que {@link #delete(String)} pero no interrumpe el flujo si el borrado falla. */
    default void deleteQuietly(String publicUrl) {
        try {
            delete(publicUrl);
        } catch (Exception ignored) {
            // El borrado no debe romper la operación principal (crear reporte, actualizar logo, etc.).
        }
    }
}
