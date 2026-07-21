package com.behsazan.schemaforge.dialect.oracle;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DdlGenerationPolicy;
import com.behsazan.schemaforge.domain.enums.IndexType;
import com.behsazan.schemaforge.domain.enums.ReferentialAction;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.generation.ddl.generator.table.TableGenerationException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class OracleDdlGenerationPolicy implements DdlGenerationPolicy {
    private static final Set<String> PHYSICAL_OPTIONS = Set.of(
            "TABLESPACE", "PCTFREE", "INITRANS", "MAXTRANS", "LOGGING", "COMPRESS");

    @Override
    public String renderForeignKeyActions(ReferentialAction onDelete, ReferentialAction onUpdate) {
        if (onUpdate != ReferentialAction.NO_ACTION) {
            throw new IllegalArgumentException("Oracle does not support ON UPDATE actions for foreign keys");
        }
        return switch (onDelete) {
            case NO_ACTION -> "";
            case CASCADE -> "\nON DELETE CASCADE";
            case SET_NULL -> "\nON DELETE SET NULL";
            case RESTRICT, SET_DEFAULT -> throw new IllegalArgumentException(
                    "Oracle does not support ON DELETE " + onDelete.name().replace('_', ' '));
        };
    }

    @Override public boolean qualifyIndexNameWithSchema() { return true; }

    @Override
    public String indexTypePrefix(IndexType type) {
        return switch (type) {
            case NORMAL -> "";
            case UNIQUE -> "UNIQUE ";
            case BITMAP -> "BITMAP ";
            case FUNCTION_BASED, CLUSTERED, NONCLUSTERED -> throw new IllegalArgumentException(
                    "Oracle index type is not supported by Index Engine v1: " + type);
        };
    }

    @Override public String noMinValueClause() { return " NOMINVALUE"; }
    @Override public String noMaxValueClause() { return " NOMAXVALUE"; }
    @Override public String noCycleClause() { return " NOCYCLE"; }
    @Override public String noCacheClause() { return " NOCACHE"; }

    @Override
    public String renderPhysicalOptions(Map<String, String> options) {
        if (options == null || options.isEmpty()) return "";
        StringBuilder result = new StringBuilder();
        options.forEach((rawKey, rawValue) -> {
            String key = rawKey.trim().toUpperCase(Locale.ROOT);
            String value = rawValue == null ? "" : rawValue.trim();
            if (!PHYSICAL_OPTIONS.contains(key)) {
                throw new IllegalArgumentException("Unsupported Oracle physical option: " + key);
            }
            result.append("\n").append(renderOption(key, value));
        });
        return result.toString();
    }

    @Override
    public String renderDataType(DataType dataType, DatabaseDialect dialect) {
        String baseName = dialect.dataTypeRules().normalize(dataType.name().value());
        if (!dialect.dataTypeRules().supports(baseName)) {
            throw new TableGenerationException("Unsupported data type for " + dialect.name() + ": " + baseName);
        }
        return withSize(baseName, dataType);
    }

    private static String withSize(String baseName, DataType dataType) {
        if (dataType.length() != null) return baseName + "(" + dataType.length() + ")";
        if (dataType.precision() != null) {
            return dataType.scale() == null
                    ? baseName + "(" + dataType.precision() + ")"
                    : baseName + "(" + dataType.precision() + "," + dataType.scale() + ")";
        }
        return baseName;
    }

    private static String renderOption(String key, String value) {
        if (key.equals("LOGGING")) return truthy(value) ? "LOGGING" : "NOLOGGING";
        if (key.equals("COMPRESS")) return truthy(value) ? "COMPRESS" : "NOCOMPRESS";
        if (value.isBlank()) throw new IllegalArgumentException("Physical option " + key + " requires a value");
        if (Set.of("PCTFREE", "INITRANS", "MAXTRANS").contains(key) && !value.matches("\\d+")) {
            throw new IllegalArgumentException("Physical option " + key + " must be numeric");
        }
        return key + " " + value;
    }

    private static boolean truthy(String value) {
        return value.isBlank() || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equals("1")
                || value.equalsIgnoreCase("logging") || value.equalsIgnoreCase("compress");
    }
}
