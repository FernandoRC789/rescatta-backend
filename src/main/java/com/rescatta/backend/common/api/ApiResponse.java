package com.rescatta.backend.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;

/**
 * Envoltorio estándar para todas las respuestas de la API, para que el cliente (la app
 * iOS) siempre reciba la misma forma de respuesta sin importar el endpoint:
 *
 * <pre>
 * {
 *   "success": true,
 *   "message": "Reporte creado correctamente",
 *   "data": { ... },
 *   "timestamp": "2026-08-13T10:00:00Z"
 * }
 * </pre>
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final Instant timestamp = Instant.now();

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, null, data);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
