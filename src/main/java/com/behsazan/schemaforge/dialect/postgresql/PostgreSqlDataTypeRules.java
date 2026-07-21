package com.behsazan.schemaforge.dialect.postgresql;

import com.behsazan.schemaforge.dialect.DataTypeRules;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class PostgreSqlDataTypeRules implements DataTypeRules {
    private static final Map<String,String> ALIASES = Map.ofEntries(
            Map.entry("VARCHAR2", "VARCHAR"), Map.entry("NVARCHAR2", "VARCHAR"),
            Map.entry("NUMBER", "NUMERIC"), Map.entry("BINARY_FLOAT", "REAL"),
            Map.entry("BINARY_DOUBLE", "DOUBLE PRECISION"), Map.entry("CLOB", "TEXT"),
            Map.entry("NCLOB", "TEXT"), Map.entry("BLOB", "BYTEA"), Map.entry("RAW", "BYTEA"),
            Map.entry("LONG RAW", "BYTEA"), Map.entry("LONG", "TEXT"));
    private static final Set<String> TYPES = Set.of(
            "BIGINT", "BIGSERIAL", "BIT", "BIT VARYING", "BOOLEAN", "BYTEA", "CHAR", "CHARACTER",
            "CHARACTER VARYING", "DATE", "DECIMAL", "DOUBLE PRECISION", "INTEGER", "INTERVAL", "JSON", "JSONB",
            "NUMERIC", "REAL", "SERIAL", "SMALLINT", "SMALLSERIAL", "TEXT", "TIME", "TIME WITH TIME ZONE",
            "TIMESTAMP", "TIMESTAMP WITH TIME ZONE", "UUID", "VARCHAR", "XML");
    @Override public boolean supports(String dataTypeName) {
        if (dataTypeName == null) return false;
        String normalized = normalize(dataTypeName);
        String base = normalized.replaceFirst("\\s*\\(.*$", "");
        return TYPES.contains(base);
    }
    @Override public Set<String> supportedDataTypes() { return TYPES; }
    @Override public String normalize(String dataTypeName) {
        if (dataTypeName == null) return null;
        String value = dataTypeName.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
        return ALIASES.getOrDefault(value, value);
    }
}
