package com.rescatta.backend.common.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Abstracción de almacenamiento de archivos. Hoy la implementación es en disco local
 * ({@link LocalFileStorageService}), pero cualquier controller/service que dependa de esta
 * interfaz no se entera si mañana cambia a Firebase Storage, S3, o Cloudinary — ese es
 * justamente el punto de aplicar el principio de inversión de dependencias aquí.
 */
public interface FileStorageService {

    /**
     * Guarda un único archivo dentro de la subcarpeta indicada y devuelve la URL pública
     * con la que el cliente podrá acceder a él.
     */
    String store(MultipartFile file, String subfolder);

    /** Guarda varios archivos y devuelve sus URLs públicas, en el mismo orden recibido. */
    List<String> storeAll(List<MultipartFile> files, String subfolder);

    /** Elimina un archivo previamente almacenado, dada su URL pública. */
    void delete(String publicUrl);
}
