package com.behsazan.schemaforge.generation.ddl.generator.table;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import java.util.Objects;

public final class DataTypeSqlRenderer {

    public String render(DataType dataType, DatabaseDialect dialect) {
        Objects.requireNonNull(dataType, "dataType must not be null");
        Objects.requireNonNull(dialect, "dialect must not be null");
        String originalName = dataType.name().value();
        String baseName = dialect.dataTypeRules().normalize(originalName);
        if (dialect.product() == DatabaseProduct.POSTGRESQL
                && originalName.equalsIgnoreCase("NUMBER")
                && dataType.scale() != null && dataType.scale() == 0
                && dataType.precision() != null && dataType.precision() <= 19) {
            return dataType.precision() <= 9 ? "INTEGER" : "BIGINT";
        }
        if (!dialect.dataTypeRules().supports(baseName)) {
            throw new TableGenerationException("Unsupported data type for " + dialect.name() + ": " + baseName);
        }
        if (dataType.length() != null) {
            return baseName + "(" + dataType.length() + ")";
        }
        if (dataType.precision() != null) {
            return dataType.scale() == null
                    ? baseName + "(" + dataType.precision() + ")"
                    : baseName + "(" + dataType.precision() + "," + dataType.scale() + ")";
        }
        return baseName;
    }
}
