package com.behsazan.schemaforge.generation.ddl.renderer.oracle;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatement;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatementType;
import com.behsazan.schemaforge.generation.ddl.model.RenderContext;
import com.behsazan.schemaforge.generation.ddl.renderer.AbstractDdlRenderer;
import com.behsazan.schemaforge.generation.ddl.renderer.SqlWriter;

public final class OracleDdlRenderer extends AbstractDdlRenderer {

    @Override
    public DatabaseProduct product() {
        return DatabaseProduct.ORACLE;
    }

    @Override
    protected String statementTerminator(DdlStatement statement, RenderContext context) {
        if (statement.type() == DdlStatementType.CREATE_TRIGGER) {
            return context.options().lineSeparator() + "/";
        }
        return super.statementTerminator(statement, context);
    }

    @Override
    protected boolean hasTerminator(String sql, DdlStatement statement, RenderContext context) {
        String normalized = sql.stripTrailing();
        if (statement.type() == DdlStatementType.CREATE_TRIGGER) {
            return normalized.endsWith("/");
        }
        return normalized.endsWith(context.dialect().ddlSyntax().statementTerminator());
    }

    @Override
    protected void writePreamble(SqlWriter writer, RenderContext context) {
        if (context.options().includePreamble()) {
            writer.line("WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK");
            writer.blankLine();
        }
    }

    @Override
    protected void writePostamble(SqlWriter writer, RenderContext context) {
        if (context.options().includePostamble()) {
            writer.line("EXIT SUCCESS");
        }
    }
}
