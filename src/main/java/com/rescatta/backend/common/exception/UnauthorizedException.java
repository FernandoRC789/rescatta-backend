package com.rescatta.backend.common.exception;

/**
 * Se lanza cuando la request no trae identidad de usuario válida (ver
 * {@link com.rescatta.backend.common.security.CurrentUserProvider}).
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
