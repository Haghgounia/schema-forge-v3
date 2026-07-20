package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.domain.valueobject.DataType;
import java.util.Locale;
import java.util.Set;

public final class OracleCanonicalTypeMapper {
    private static final Set<String> DIRECT = Set.of(
            "DATE", "TIMESTAMP", "CLOB", "NCLOB", "BLOB", "RAW", "LONG", "XMLTYPE", "JSON");

    public String map(DataType type) {
        String name = type.name().normalized();
        return switch (name) {
            case "STRING", "VARCHAR", "VARCHAR2", "CHARACTER VARYING" -> varchar2(type);
            case "NVARCHAR", "NVARCHAR2" -> "NVARCHAR2(" + valueOrDefault(type.length(), 200) + ")";
            case "CHAR", "NCHAR" -> name + "(" + valueOrDefault(type.length(), 1) + ")";
            case "INTEGER", "INT", "BIGINT", "SMALLINT", "DECIMAL", "NUMERIC", "NUMBER" -> number(type);
            case "BOOLEAN" -> "NUMBER(1)";
            case "DATETIME" -> "TIMESTAMP";
            case "TIMESTAMP_WITH_TIME_ZONE" -> timestamp("TIMESTAMP WITH TIME ZONE", type);
            case "TIMESTAMP_WITH_LOCAL_TIME_ZONE" -> timestamp("TIMESTAMP WITH LOCAL TIME ZONE", type);
            case "LONG_RAW" -> "LONG RAW";
            default -> DIRECT.contains(name) ? withOptionalLength(name, type) : original(type);
        };
    }

    private String varchar2(DataType type) {
        return "VARCHAR2(" + valueOrDefault(type.length(), 4000) + " CHAR)";
    }

    private String number(DataType type) {
        if (type.precision() == null) return "NUMBER";
        if (type.scale() == null) return "NUMBER(" + type.precision() + ")";
        return "NUMBER(" + type.precision() + "," + type.scale() + ")";
    }

    private String withOptionalLength(String name, DataType type) {
        if (name.equals("TIMESTAMP") && type.precision() != null) {
            return name + "(" + type.precision() + ")";
        }
        return type.length() == null ? name : name + "(" + type.length() + ")";
    }

    private String timestamp(String name, DataType type) {
        return type.precision() == null ? name : "TIMESTAMP(" + type.precision() + ")" + name.substring("TIMESTAMP".length());
    }

    private String original(DataType type) {
        String name = type.name().value().toUpperCase(Locale.ROOT);
        if (type.length() != null) return name + "(" + type.length() + ")";
        if (type.precision() != null && type.scale() != null) return name + "(" + type.precision() + "," + type.scale() + ")";
        if (type.precision() != null) return name + "(" + type.precision() + ")";
        return name;
    }

    private int valueOrDefault(Integer value, int fallback) {
        return value == null ? fallback : value;
    }
}
