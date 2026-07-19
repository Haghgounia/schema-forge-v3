package com.behsazan.schemaforge.specification.domain;

import java.util.Objects;

public record DataTypeDefinition(String name, Integer length, Integer precision, Integer scale) {
    public DataTypeDefinition {
        Objects.requireNonNull(name, "name must not be null");
    }

    public static DataTypeDefinition of(String name) {
        return new DataTypeDefinition(name, null, null, null);
    }
}
