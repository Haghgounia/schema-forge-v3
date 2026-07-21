package com.behsazan.schemaforge.dialect.standard;

import com.behsazan.schemaforge.dialect.LogicalDataType;
import com.behsazan.schemaforge.dialect.SqlTypeMapper;

import java.util.Objects;

public final class StandardSqlTypeMapper implements SqlTypeMapper {

    @Override
    public String map(LogicalDataType type) {
        return map(type, null, null, null);
    }

    @Override
    public String map(LogicalDataType type, Integer length, Integer precision, Integer scale) {
        Objects.requireNonNull(type, "type must not be null");
        return switch (type) {
            case STRING -> "VARCHAR(" + positiveOrDefault(length, 255) + ")";
            case NATIONAL_STRING -> "NATIONAL VARCHAR(" + positiveOrDefault(length, 255) + ")";
            case INTEGER -> "INTEGER";
            case BIG_INTEGER -> "BIGINT";
            case DECIMAL -> "DECIMAL(" + positiveOrDefault(precision, 38) + "," + nonNegativeOrDefault(scale, 0) + ")";
            case BOOLEAN -> "BOOLEAN";
            case DATE -> "DATE";
            case TIME -> "TIME";
            case TIMESTAMP -> "TIMESTAMP";
            case BINARY -> "VARBINARY(" + positiveOrDefault(length, 2000) + ")";
            case LARGE_TEXT -> "CLOB";
            case LARGE_BINARY -> "BLOB";
        };
    }

    private int positiveOrDefault(Integer value, int defaultValue) {
        return value == null || value <= 0 ? defaultValue : value;
    }

    private int nonNegativeOrDefault(Integer value, int defaultValue) {
        return value == null || value < 0 ? defaultValue : value;
    }
}
