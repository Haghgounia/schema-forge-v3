package com.behsazan.schemaforge.dialect.oracle;

import com.behsazan.schemaforge.dialect.LogicalDataType;
import com.behsazan.schemaforge.dialect.SqlTypeMapper;

import java.util.Objects;

public final class OracleSqlTypeMapper implements SqlTypeMapper {

    @Override
    public String map(LogicalDataType type) {
        return map(type, null, null, null);
    }

    @Override
    public String map(LogicalDataType type, Integer length, Integer precision, Integer scale) {
        Objects.requireNonNull(type, "type must not be null");
        return switch (type) {
            case STRING -> "VARCHAR2(" + positiveOrDefault(length, 255) + " CHAR)";
            case NATIONAL_STRING -> "NVARCHAR2(" + positiveOrDefault(length, 255) + ")";
            case INTEGER -> "NUMBER(10)";
            case BIG_INTEGER -> "NUMBER(19)";
            case DECIMAL -> decimal(precision, scale);
            case BOOLEAN -> "NUMBER(1)";
            case DATE -> "DATE";
            case TIME, TIMESTAMP -> "TIMESTAMP";
            case BINARY -> "RAW(" + positiveOrDefault(length, 2000) + ")";
            case LARGE_TEXT -> "CLOB";
            case LARGE_BINARY -> "BLOB";
        };
    }

    private String decimal(Integer precision, Integer scale) {
        int resolvedPrecision = positiveOrDefault(precision, 38);
        int resolvedScale = scale == null ? 0 : Math.max(scale, 0);
        if (resolvedScale > resolvedPrecision) {
            throw new IllegalArgumentException("scale must not be greater than precision");
        }
        return "NUMBER(" + resolvedPrecision + "," + resolvedScale + ")";
    }

    private int positiveOrDefault(Integer value, int defaultValue) {
        return value == null || value <= 0 ? defaultValue : value;
    }
}
