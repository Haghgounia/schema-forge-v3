package com.behsazan.schemaforge.specification.domain;

import java.util.List;
import java.util.Objects;

public record IndexDefinition(String name, List<String> columns, boolean unique) {
    public IndexDefinition {
        columns = List.copyOf(Objects.requireNonNull(columns, "columns must not be null"));
    }
}
