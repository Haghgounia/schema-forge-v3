package com.behsazan.schemaforge.generation.ddl.renderer.postgresql;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.generation.ddl.model.RenderContext;
import com.behsazan.schemaforge.generation.ddl.renderer.AbstractDdlRenderer;
import com.behsazan.schemaforge.generation.ddl.renderer.SqlWriter;

public final class PostgreSqlDdlRenderer extends AbstractDdlRenderer {
    @Override public DatabaseProduct product() { return DatabaseProduct.POSTGRESQL; }
    @Override protected void writePreamble(SqlWriter writer, RenderContext context) {
        if (context.options().includePreamble()) {
            writer.line("\\set ON_ERROR_STOP on");
            writer.blankLine();
        }
    }
}
