package com.behsazan.schemaforge.dialect.postgresql;

import com.behsazan.schemaforge.dialect.LogicalDataType;
import com.behsazan.schemaforge.dialect.SqlTypeMapper;
import java.util.Objects;

public final class PostgreSqlSqlTypeMapper implements SqlTypeMapper {
    @Override public String map(LogicalDataType type) { return map(type, null, null, null); }
    @Override public String map(LogicalDataType type, Integer length, Integer precision, Integer scale) {
        Objects.requireNonNull(type, "type must not be null");
        return switch (type) {
            case STRING, NATIONAL_STRING -> "VARCHAR(" + positive(length, 255) + ")";
            case INTEGER -> "INTEGER";
            case BIG_INTEGER -> "BIGINT";
            case DECIMAL -> "NUMERIC(" + positive(precision, 38) + "," + nonNegative(scale, 0) + ")";
            case BOOLEAN -> "BOOLEAN";
            case DATE -> "DATE";
            case TIME -> "TIME";
            case TIMESTAMP -> "TIMESTAMP";
            case BINARY, LARGE_BINARY -> "BYTEA";
            case LARGE_TEXT -> "TEXT";
        };
    }
    private static int positive(Integer value, int fallback) { return value == null || value <= 0 ? fallback : value; }
    private static int nonNegative(Integer value, int fallback) { return value == null || value < 0 ? fallback : value; }
}
