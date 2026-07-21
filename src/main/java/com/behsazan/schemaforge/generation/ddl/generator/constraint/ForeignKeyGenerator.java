package com.behsazan.schemaforge.generation.ddl.generator.constraint;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
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
        if (dialect.product() == DatabaseProduct.ORACLE) {
            appendOracleDeleteAction(ddl, foreignKey.onDelete());
            if (foreignKey.onUpdate() != ReferentialAction.NO_ACTION) {
                throw new IllegalArgumentException("Oracle does not support ON UPDATE actions for foreign keys");
            }
            return;
        }
        appendStandardAction(ddl, "ON DELETE", foreignKey.onDelete());
        appendStandardAction(ddl, "ON UPDATE", foreignKey.onUpdate());
    }

    private static void appendOracleDeleteAction(StringBuilder ddl, ReferentialAction action) {
        switch (action) {
            case NO_ACTION -> { }
            case CASCADE -> ddl.append("\nON DELETE CASCADE");
            case SET_NULL -> ddl.append("\nON DELETE SET NULL");
            case RESTRICT, SET_DEFAULT -> throw new IllegalArgumentException(
                    "Oracle does not support ON DELETE " + action.name().replace('_', ' '));
        }
    }

    private static void appendStandardAction(StringBuilder ddl, String clause, ReferentialAction action) {
        if (action != ReferentialAction.NO_ACTION) {
            ddl.append("\n").append(clause).append(" ").append(action.name().replace('_', ' '));
        }
    }
}
