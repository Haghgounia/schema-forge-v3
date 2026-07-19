package com.behsazan.schemaforge.generation.model;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record SqlSection(String name, int order, List<SqlStatement> statements) {
    public SqlSection {
        name = Objects.requireNonNull(name, "name must not be null").strip();
        if (name.isEmpty()) throw new IllegalArgumentException("name must not be blank");
        statements = statements == null ? List.of() : statements.stream()
                .sorted(Comparator.comparingInt(SqlStatement::order))
                .toList();
    }
}
