package com.rescatta.backend.common.exception;

/** Se lanza cuando el usuario autenticado no tiene el rol/permiso requerido para la acción. */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
