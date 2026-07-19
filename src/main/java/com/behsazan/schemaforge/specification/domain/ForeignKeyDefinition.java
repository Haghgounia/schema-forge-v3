package com.behsazan.schemaforge.specification.domain;

import java.util.List;
import java.util.Objects;

public record ForeignKeyDefinition(
        String name,
        List<String> columns,
        String referencedSchema,
        String referencedTable,
        List<String> referencedColumns) {
    public ForeignKeyDefinition {
        columns = List.copyOf(Objects.requireNonNull(columns, "columns must not be null"));
        referencedColumns = List.copyOf(Objects.requireNonNull(referencedColumns, "referencedColumns must not be null"));
        Objects.requireNonNull(referencedTable, "referencedTable must not be null");
    }
}
