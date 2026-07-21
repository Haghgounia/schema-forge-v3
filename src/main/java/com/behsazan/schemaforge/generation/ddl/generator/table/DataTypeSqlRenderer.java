package com.behsazan.schemaforge.generation.ddl.generator.table;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import java.util.Objects;

public final class DataTypeSqlRenderer {
    public String render(DataType dataType, DatabaseDialect dialect) {
        Objects.requireNonNull(dataType, "dataType must not be null");
        Objects.requireNonNull(dialect, "dialect must not be null");
        return dialect.ddlGenerationPolicy().renderDataType(dataType, dialect);
    }
}
