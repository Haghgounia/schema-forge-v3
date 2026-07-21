package com.behsazan.schemaforge.generation.ddl.model;

import java.util.Locale;
import java.util.Objects;

public record DdlObjectReference(String schemaName, String objectName, String objectType) {
    public DdlObjectReference {
        schemaName = normalizeOptional(schemaName);
        objectName = normalizeRequired(objectName, "objectName");
        objectType = normalizeRequired(objectType, "objectType").toUpperCase(Locale.ROOT);
    }

    public String qualifiedName() {
        return schemaName.isEmpty() ? objectName : schemaName + "." + objectName;
    }

    private static String normalizeRequired(String value, String fieldName) {
        String normalized = Objects.requireNonNull(value, fieldName + " must not be null").strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private static String normalizeOptional(String value) {
        return value == null ? "" : value.strip();
    }
}
