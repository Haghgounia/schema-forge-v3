package com.behsazan.schemaforge.generation.ddl.generator.table;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.domain.model.Column;

/** Vendor extension point for rendering a column definition. */
public interface ColumnDefinitionGenerator {
    DatabaseProduct product();

    String generate(Column column, DatabaseDialect dialect);
}
