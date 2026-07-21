package com.behsazan.schemaforge.generation.ddl.model;

import java.util.Objects;

public record DdlGenerationMessage(
        DdlGenerationSeverity severity,
        String code,
        String message,
        String objectName) {

    public DdlGenerationMessage {
        Objects.requireNonNull(severity, "severity must not be null");
        code = normalizeRequired(code, "code");
        message = normalizeRequired(message, "message");
        objectName = objectName == null ? "" : objectName.strip();
    }

    private static String normalizeRequired(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
