package com.rescatta.backend.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final boolean success = false;
    private final String message;
    private final Instant timestamp = Instant.now();
    private final List<FieldValidationError> fieldErrors;

    public ErrorResponse(String message) {
        this(message, null);
    }

    public ErrorResponse(String message, List<FieldValidationError> fieldErrors) {
        this.message = message;
        this.fieldErrors = fieldErrors;
    }

    public record FieldValidationError(String field, String reason) {
        public static FieldValidationError fromMap(Map.Entry<String, String> entry) {
            return new FieldValidationError(entry.getKey(), entry.getValue());
        }
    }
}
