package com.behsazan.schemaforge.generation.ddl.generator.comment;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.generation.ddl.generator.table.IdentifierSqlRenderer;
import com.behsazan.schemaforge.generation.ddl.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CommentGenerator {
    private final IdentifierSqlRenderer identifiers = new IdentifierSqlRenderer();

    public List<DdlStatement> generate(Table table, DatabaseDialect dialect, int startPosition) {
        Objects.requireNonNull(table); Objects.requireNonNull(dialect);
        List<DdlStatement> result = new ArrayList<>();
        int position = startPosition;
        if (!table.description().isEmpty()) {
            result.add(statement(table, position++, "COMMENT ON TABLE " + identifiers.render(table.qualifiedName(), dialect)
                    + " IS '" + escape(table.description().value()) + "'"));
        }
        for (var column : table.columns()) {
            if (!column.description().isEmpty()) {
                String target = identifiers.render(table.qualifiedName(), dialect) + "."
                        + identifiers.render(column.name(), dialect);
                result.add(statement(table, position++, "COMMENT ON COLUMN " + target + " IS '"
                        + escape(column.description().value()) + "'"));
            }
        }
        return List.copyOf(result);
    }

    private static String escape(String value) { return value.replace("'", "''"); }
    private static DdlStatement statement(Table table, int position, String sql) {
        return DdlStatement.of(DdlStatementType.COMMENT,
                new DdlObjectReference(table.qualifiedName().schemaName().map(Object::toString).orElse(""),
                        table.qualifiedName().name().toString(), "TABLE"),
                new StatementOrder(DdlPhase.COMMENTS, position), SqlFragment.of(sql));
    }
}
