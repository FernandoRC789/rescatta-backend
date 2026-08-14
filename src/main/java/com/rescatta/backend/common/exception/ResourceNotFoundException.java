package com.rescatta.backend.common.exception;

/** Se lanza cuando un recurso solicitado (reporte, mascota, raza, etc.) no existe. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String entity, Object id) {
        return new ResourceNotFoundException("%s no encontrado con id: %s".formatted(entity, id));
    }
}
