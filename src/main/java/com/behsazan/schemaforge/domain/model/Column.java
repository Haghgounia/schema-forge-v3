package com.behsazan.schemaforge.domain.model;

import com.behsazan.schemaforge.domain.valueobject.*;
import java.util.Objects;

public record Column(Identifier name, DataType dataType, boolean nullable, DefaultValue defaultValue,
                     Description description, boolean identity, Integer ordinalPosition) {
    public Column {
        Objects.requireNonNull(name, "column name must not be null");
        Objects.requireNonNull(dataType, "column dataType must not be null");
        defaultValue = defaultValue == null ? new DefaultValue(null) : defaultValue;
        description = description == null ? Description.empty() : description;
        if (ordinalPosition != null && ordinalPosition <= 0) throw new IllegalArgumentException("ordinalPosition must be positive");
    }
    public static Column required(String name, DataType type) {
        return new Column(Identifier.of(name), type, false, null, null, false, null);
    }
    public static Column nullable(String name, DataType type) {
        return new Column(Identifier.of(name), type, true, null, null, false, null);
    }
}
