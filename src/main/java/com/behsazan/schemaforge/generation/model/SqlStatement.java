package com.behsazan.schemaforge.generation.model;

import java.util.Objects;

public record SqlStatement(String sql, String objectType, String objectName, int order) {
    public SqlStatement {
        sql = Objects.requireNonNull(sql, "sql must not be null").strip();
        objectType = objectType == null ? "UNKNOWN" : objectType;
        objectName = objectName == null ? "" : objectName;
        if (sql.isEmpty()) throw new IllegalArgumentException("sql must not be blank");
    }
}
