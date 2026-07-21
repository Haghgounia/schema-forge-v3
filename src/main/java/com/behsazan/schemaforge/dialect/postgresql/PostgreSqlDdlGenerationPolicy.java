package com.behsazan.schemaforge.dialect.postgresql;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DdlGenerationPolicy;
import com.behsazan.schemaforge.domain.enums.IndexType;
import com.behsazan.schemaforge.domain.enums.ReferentialAction;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.generation.ddl.generator.table.TableGenerationException;
import java.util.Locale;
import java.util.Map;

public final class PostgreSqlDdlGenerationPolicy implements DdlGenerationPolicy {
    @Override
    public String renderForeignKeyActions(ReferentialAction onDelete, ReferentialAction onUpdate) {
        return renderAction("ON DELETE", onDelete) + renderAction("ON UPDATE", onUpdate);
    }

    private static String renderAction(String clause, ReferentialAction action) {
        return action == ReferentialAction.NO_ACTION
                ? ""
                : "\n" + clause + " " + action.name().replace('_', ' ');
    }

    @Override public boolean qualifyIndexNameWithSchema() { return false; }

    @Override
    public String indexTypePrefix(IndexType type) {
        return switch (type) {
            case NORMAL -> "";
            case UNIQUE -> "UNIQUE ";
            case BITMAP, FUNCTION_BASED, CLUSTERED, NONCLUSTERED -> throw new IllegalArgumentException(
                    "Index type is not supported by PostgreSQL in Index Engine v1: " + type);
        };
    }

    @Override public String noMinValueClause() { return " NO MINVALUE"; }
    @Override public String noMaxValueClause() { return " NO MAXVALUE"; }
    @Override public String noCycleClause() { return " NO CYCLE"; }
    @Override public String noCacheClause() { return ""; }

    @Override
    public String renderPhysicalOptions(Map<String, String> options) {
        if (options == null || options.isEmpty()) return "";
        StringBuilder result = new StringBuilder();
        options.forEach((rawKey, rawValue) -> {
            String key = rawKey.trim().toUpperCase(Locale.ROOT);
            String value = rawValue == null ? "" : rawValue.trim();
            if (!key.equals("TABLESPACE")) {
                throw new IllegalArgumentException("Unsupported PostgreSQL physical option: " + key);
            }
            if (value.isBlank()) {
                throw new IllegalArgumentException("Physical option TABLESPACE requires a value");
            }
            result.append("\nTABLESPACE ").append(value);
        });
        return result.toString();
    }

    @Override
    public String renderDataType(DataType dataType, DatabaseDialect dialect) {
        String originalName = dataType.name().value();
        String baseName = dialect.dataTypeRules().normalize(originalName);
        if (originalName.equalsIgnoreCase("NUMBER")
                && dataType.scale() != null && dataType.scale() == 0
                && dataType.precision() != null && dataType.precision() <= 19) {
            return dataType.precision() <= 9 ? "INTEGER" : "BIGINT";
        }
        if (!dialect.dataTypeRules().supports(baseName)) {
            throw new TableGenerationException("Unsupported data type for " + dialect.name() + ": " + baseName);
        }
        if (dataType.length() != null) return baseName + "(" + dataType.length() + ")";
        if (dataType.precision() != null) {
            return dataType.scale() == null
                    ? baseName + "(" + dataType.precision() + ")"
                    : baseName + "(" + dataType.precision() + "," + dataType.scale() + ")";
        }
        return baseName;
    }
}
