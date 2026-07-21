package com.behsazan.schemaforge.generation.ddl.model;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public record DdlSection(String name, DdlPhase phase, List<DdlStatement> statements) {
    public DdlSection {
        name = Objects.requireNonNull(name, "name must not be null").strip();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        Objects.requireNonNull(phase, "phase must not be null");
        statements = statements == null ? List.of() : statements.stream()
                .sorted(Comparator.comparing(DdlStatement::order))
                .toList();
        if (statements.stream().anyMatch(statement -> statement.order().phase() != phase)) {
            throw new IllegalArgumentException("all statements must belong to the section phase");
        }
    }

    public boolean isEmpty() {
        return statements.isEmpty();
    }
}
