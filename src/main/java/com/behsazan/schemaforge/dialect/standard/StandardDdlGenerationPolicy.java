package com.behsazan.schemaforge.dialect.standard;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DdlGenerationPolicy;
import com.behsazan.schemaforge.domain.enums.IndexType;
import com.behsazan.schemaforge.domain.enums.ReferentialAction;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.generation.ddl.generator.table.TableGenerationException;
import java.util.Map;

public final class StandardDdlGenerationPolicy implements DdlGenerationPolicy {
    @Override
    public String renderForeignKeyActions(ReferentialAction onDelete, ReferentialAction onUpdate) {
        return action("ON DELETE", onDelete) + action("ON UPDATE", onUpdate);
    }

    private static String action(String clause, ReferentialAction value) {
        return value == ReferentialAction.NO_ACTION ? "" : "\n" + clause + " " + value.name().replace('_', ' ');
    }

    @Override public boolean qualifyIndexNameWithSchema() { return false; }
    @Override public String indexTypePrefix(IndexType type) {
        return switch (type) {
            case NORMAL -> "";
            case UNIQUE -> "UNIQUE ";
            default -> throw new IllegalArgumentException("Index type is not supported by Standard SQL: " + type);
        };
    }
    @Override public String noMinValueClause() { return " NO MINVALUE"; }
    @Override public String noMaxValueClause() { return " NO MAXVALUE"; }
    @Override public String noCycleClause() { return " NO CYCLE"; }
    @Override public String noCacheClause() { return ""; }
    @Override public String renderPhysicalOptions(Map<String, String> options) {
        if (options == null || options.isEmpty()) return "";
        throw new IllegalArgumentException("Physical options are not supported by Standard SQL");
    }
    @Override
    public String renderDataType(DataType dataType, DatabaseDialect dialect) {
        String baseName = dialect.dataTypeRules().normalize(dataType.name().value());
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
