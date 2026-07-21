package com.behsazan.schemaforge.generation.ddl.generator.constraint;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.enums.ReferentialAction;
import com.behsazan.schemaforge.domain.model.ForeignKey;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.generation.ddl.generator.table.IdentifierSqlRenderer;
import com.behsazan.schemaforge.generation.ddl.model.DdlPhase;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatement;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatementType;
import com.behsazan.schemaforge.generation.ddl.model.SqlFragment;
import com.behsazan.schemaforge.generation.ddl.model.StatementOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ForeignKeyGenerator {
    private final ConstraintSqlSupport sql = new ConstraintSqlSupport();
    private final IdentifierSqlRenderer identifiers = new IdentifierSqlRenderer();

    public List<DdlStatement> generate(Table table, DatabaseDialect dialect, int startingPosition) {
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(dialect, "dialect must not be null");
        if (startingPosition < 0) {
            throw new IllegalArgumentException("startingPosition must not be negative");
        }
        List<DdlStatement> statements = new ArrayList<>();
        for (int index = 0; index < table.foreignKeys().size(); index++) {
            statements.add(statement(table, table.foreignKeys().get(index), dialect, startingPosition + index));
        }
        return List.copyOf(statements);
    }

    private DdlStatement statement(Table table, ForeignKey foreignKey, DatabaseDialect dialect, int position) {
        Identifier name = foreignKey.name() == null
                ? Identifier.of(dialect.namingStrategy().foreignKey(
                        table.qualifiedName().name().value(),
                        foreignKey.referencedTable().name().value()))
                : foreignKey.name();

        StringBuilder ddl = new StringBuilder()
                .append("ALTER TABLE ").append(sql.tableName(table, dialect))
                .append("\nADD CONSTRAINT ").append(sql.identifier(name, dialect))
                .append("\nFOREIGN KEY (").append(sql.columns(foreignKey.columns(), dialect)).append(")")
                .append("\nREFERENCES ").append(identifiers.render(foreignKey.referencedTable(), dialect))
                .append(" (").append(sql.columns(foreignKey.referencedColumns(), dialect)).append(")");

        appendActions(ddl, foreignKey, dialect);

        return DdlStatement.of(
                DdlStatementType.CREATE_FOREIGN_KEY,
                sql.reference(table, "FOREIGN_KEY"),
                new StatementOrder(DdlPhase.FOREIGN_KEYS, position),
                SqlFragment.of(ddl.toString()));
    }

    private static void appendActions(StringBuilder ddl, ForeignKey foreignKey, DatabaseDialect dialect) {
        ddl.append(dialect.ddlGenerationPolicy().renderForeignKeyActions(
                foreignKey.onDelete(), foreignKey.onUpdate()));
    }
}
