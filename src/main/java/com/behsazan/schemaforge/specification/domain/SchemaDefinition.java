package com.behsazan.schemaforge.specification.domain;

import java.util.List;
import java.util.Objects;

public record SchemaDefinition(String name, List<TableDefinition> tables) {
    public SchemaDefinition {
        tables = List.copyOf(Objects.requireNonNull(tables, "tables must not be null"));
    }
}
