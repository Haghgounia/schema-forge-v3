package com.behsazan.schemaforge.generation.ddl.renderer;

import com.behsazan.schemaforge.generation.ddl.model.DdlScript;
import com.behsazan.schemaforge.generation.ddl.model.DdlSection;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatement;
import com.behsazan.schemaforge.generation.ddl.model.RenderContext;
import com.behsazan.schemaforge.generation.ddl.model.ScriptOptions;
import com.behsazan.schemaforge.generation.ddl.model.SqlFragment;
import java.util.Objects;

public abstract class AbstractDdlRenderer implements DdlRenderer {

    @Override
    public final RenderedDdl render(DdlScript script, RenderContext context) {
        Objects.requireNonNull(script, "script must not be null");
        Objects.requireNonNull(context, "context must not be null");
        if (context.dialect().product() != product()) {
            throw new DdlRenderException(
                    "renderer product " + product() + " does not match dialect product "
                            + context.dialect().product());
        }

        SqlWriter writer = new SqlWriter(context.options().lineSeparator());
        writePreamble(writer, context);
        int count = 0;
        for (DdlSection section : script.sections()) {
            if (section.isEmpty()) {
                continue;
            }
            writeSectionHeader(writer, section, context.options());
            for (DdlStatement statement : section.statements()) {
                writeStatement(writer, statement, context);
                count++;
            }
        }
        writePostamble(writer, context);
        return new RenderedDdl(writer.content(), count);
    }

    protected void writePreamble(SqlWriter writer, RenderContext context) {
        // Default renderer does not add a preamble.
    }

    protected void writePostamble(SqlWriter writer, RenderContext context) {
        // Default renderer does not add a postamble.
    }

    protected void writeSectionHeader(SqlWriter writer, DdlSection section, ScriptOptions options) {
        if (!options.includeSectionHeaders()) {
            return;
        }
        writer.blankLine();
        writer.line("-- " + section.name());
    }

    protected void writeStatement(SqlWriter writer, DdlStatement statement, RenderContext context) {
        String separator = context.options().lineSeparator();
        String sql = statement.fragments().stream()
                .map(SqlFragment::value)
                .reduce((left, right) -> left + separator + right)
                .orElseThrow(() -> new DdlRenderException("statement has no SQL fragments"));

        writer.write(sql);
        if (context.options().terminateStatements() && !hasTerminator(sql, statement, context)) {
            writer.write(statementTerminator(statement, context));
        }
        writer.newLine().newLine();
    }

    protected String statementTerminator(DdlStatement statement, RenderContext context) {
        return context.dialect().ddlSyntax().statementTerminator();
    }

    protected boolean hasTerminator(String sql, DdlStatement statement, RenderContext context) {
        String terminator = context.dialect().ddlSyntax().statementTerminator();
        return !terminator.isEmpty() && sql.stripTrailing().endsWith(terminator);
    }
}
