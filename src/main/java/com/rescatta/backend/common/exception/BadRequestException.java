package com.rescatta.backend.common.exception;

/** Se lanza ante violaciones de reglas de negocio que no cubre {@code @Valid} (ej. más de 5 fotos). */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
