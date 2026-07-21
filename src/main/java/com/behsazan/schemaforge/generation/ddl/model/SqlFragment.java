package com.behsazan.schemaforge.generation.ddl.model;

import java.util.Objects;

public record SqlFragment(String value) {
    public SqlFragment {
        value = Objects.requireNonNull(value, "value must not be null").strip();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }

    public static SqlFragment of(String value) {
        return new SqlFragment(value);
    }
}
