package com.behsazan.schemaforge.generation.ddl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public final class SqlBuilder {
    private final List<String> parts = new ArrayList<>();

    public SqlBuilder append(String value) {
        String normalized = Objects.requireNonNull(value, "value must not be null").strip();
        if (!normalized.isEmpty()) {
            parts.add(normalized);
        }
        return this;
    }

    public SqlBuilder append(SqlFragment fragment) {
        return append(Objects.requireNonNull(fragment, "fragment must not be null").value());
    }

    public SqlBuilder appendWhen(boolean condition, String value) {
        return condition ? append(value) : this;
    }

    public SqlBuilder commaSeparated(Iterable<String> values) {
        Objects.requireNonNull(values, "values must not be null");
        StringJoiner joiner = new StringJoiner(", ");
        for (String value : values) {
            String normalized = Objects.requireNonNull(value, "value must not be null").strip();
            if (!normalized.isEmpty()) {
                joiner.add(normalized);
            }
        }
        String joined = joiner.toString();
        if (!joined.isEmpty()) {
            parts.add(joined);
        }
        return this;
    }

    public boolean isEmpty() {
        return parts.isEmpty();
    }

    public SqlFragment build() {
        if (parts.isEmpty()) {
            throw new IllegalStateException("SQL builder is empty");
        }
        return new SqlFragment(String.join(" ", parts));
    }
}
