package com.behsazan.schemaforge.generation.ddl.generator.table;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.model.Column;

public interface ColumnDefinitionGenerator {
    String generate(Column column, DatabaseDialect dialect);
}
