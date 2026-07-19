package com.behsazan.schemaforge.specification.domain;

import java.util.Objects;

public record ColumnDefinition(
        String name,
        DataTypeDefinition dataType,
        boolean nullable,
        String defaultValue,
        String description,
        boolean primaryKey,
        boolean unique,
        boolean indexed) {
    public ColumnDefinition {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(dataType, "dataType must not be null");
    }
}
