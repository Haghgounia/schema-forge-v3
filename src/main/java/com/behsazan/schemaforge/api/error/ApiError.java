package com.behsazan.schemaforge.api.error;

import java.time.Instant;
import java.util.List;

public record ApiError(
        String code,
        String message,
        String path,
        String correlationId,
        Instant timestamp,
        List<FieldViolation> violations) {

    public ApiError {
        violations = violations == null ? List.of() : List.copyOf(violations);
    }

    public record FieldViolation(String field, String message) {}
}
