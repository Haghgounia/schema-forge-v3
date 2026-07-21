package com.behsazan.schemaforge.generation.ddl.generator.storage;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class PhysicalOptionsRenderer {
    private static final Set<String> ORACLE_OPTIONS = Set.of(
            "TABLESPACE", "PCTFREE", "INITRANS", "MAXTRANS", "LOGGING", "COMPRESS");

    public String render(Map<String, String> options, DatabaseDialect dialect) {
        if (options == null || options.isEmpty()) return "";
        boolean oracle = dialect.product() == DatabaseProduct.ORACLE;
        boolean postgresql = dialect.product() == DatabaseProduct.POSTGRESQL;
        StringBuilder result = new StringBuilder();
        options.forEach((rawKey, rawValue) -> {
            String key = rawKey.trim().toUpperCase(Locale.ROOT);
            String value = rawValue == null ? "" : rawValue.trim();
            if (oracle && !ORACLE_OPTIONS.contains(key)) {
                throw new IllegalArgumentException("Unsupported Oracle physical option: " + key);
            }
            if (postgresql && !key.equals("TABLESPACE")) {
                throw new IllegalArgumentException("Unsupported PostgreSQL physical option: " + key);
            }
            result.append("\n").append(renderOption(key, value));
        });
        return result.toString();
    }

    private static String renderOption(String key, String value) {
        if (key.equals("LOGGING")) return truthy(value) ? "LOGGING" : "NOLOGGING";
        if (key.equals("COMPRESS")) return truthy(value) ? "COMPRESS" : "NOCOMPRESS";
        if (value.isBlank()) throw new IllegalArgumentException("Physical option " + key + " requires a value");
        if (Set.of("PCTFREE", "INITRANS", "MAXTRANS").contains(key) && !value.matches("\\d+"))
            throw new IllegalArgumentException("Physical option " + key + " must be numeric");
        return key + " " + value;
    }

    private static boolean truthy(String value) {
        return value.isBlank() || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equals("1")
                || value.equalsIgnoreCase("logging") || value.equalsIgnoreCase("compress");
    }
}
