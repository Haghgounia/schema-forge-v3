package com.behsazan.schemaforge.specification.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record TableDefinition(
        String schema,
        String name,
        String description,
        List<ColumnDefinition> columns,
        PrimaryKeyDefinition primaryKey,
        List<ForeignKeyDefinition> foreignKeys,
        List<IndexDefinition> indexes,
        SequenceDefinition sequence,
        Map<String, DatabaseOptions> databaseOptions) {
    public TableDefinition {
        Objects.requireNonNull(name, "name must not be null");
        columns = List.copyOf(Objects.requireNonNull(columns, "columns must not be null"));
        foreignKeys = foreignKeys == null ? List.of() : List.copyOf(foreignKeys);
        indexes = indexes == null ? List.of() : List.copyOf(indexes);
        databaseOptions = databaseOptions == null ? Map.of() : Map.copyOf(databaseOptions);
    }
}
