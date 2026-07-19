package com.behsazan.schemaforge.specification.domain;

import java.util.List;
import java.util.Objects;

public record PrimaryKeyDefinition(String name, List<String> columns) {
    public PrimaryKeyDefinition {
        columns = List.copyOf(Objects.requireNonNull(columns, "columns must not be null"));
    }
}
