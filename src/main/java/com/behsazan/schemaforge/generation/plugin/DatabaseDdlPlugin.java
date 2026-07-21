package com.behsazan.schemaforge.generation.plugin;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.generation.ddl.generator.table.ColumnDefinitionGenerator;
import com.behsazan.schemaforge.generation.ddl.renderer.DdlRenderer;

/** Complete DDL extension contributed by one database implementation. */
public interface DatabaseDdlPlugin {
    DatabaseProduct product();
    DatabaseDialect dialect();
    DdlRenderer renderer();
    ColumnDefinitionGenerator columnDefinitionGenerator();
}
