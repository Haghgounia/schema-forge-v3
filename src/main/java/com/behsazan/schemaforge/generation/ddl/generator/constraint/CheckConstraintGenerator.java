package com.behsazan.schemaforge.generation.ddl.generator.constraint;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.model.CheckConstraint;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.generation.ddl.model.DdlPhase;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatement;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatementType;
import com.behsazan.schemaforge.generation.ddl.model.SqlFragment;
import com.behsazan.schemaforge.generation.ddl.model.StatementOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class CheckConstraintGenerator {
    private final ConstraintSqlSupport sql = new ConstraintSqlSupport();

    public List<DdlStatement> generate(Table table, DatabaseDialect dialect, int startingPosition) {
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(dialect, "dialect must not be null");
        if (startingPosition < 0) {
            throw new IllegalArgumentException("startingPosition must not be negative");
        }
        List<DdlStatement> statements = new ArrayList<>();
        for (int index = 0; index < table.checkConstraints().size(); index++) {
            CheckConstraint constraint = table.checkConstraints().get(index);
            Identifier name = constraint.name() == null
                    ? defaultName(table, dialect, index + 1)
                    : constraint.name();
            String ddl = "ALTER TABLE " + sql.tableName(table, dialect)
                    + "\nADD CONSTRAINT " + sql.identifier(name, dialect)
                    + "\nCHECK (" + constraint.expression() + ")";
            statements.add(DdlStatement.of(
                    DdlStatementType.CREATE_CHECK_CONSTRAINT,
                    sql.reference(table, "CHECK_CONSTRAINT"),
                    new StatementOrder(DdlPhase.CHECK_CONSTRAINTS, startingPosition + index),
                    SqlFragment.of(ddl)));
        }
        return List.copyOf(statements);
    }

    private Identifier defaultName(Table table, DatabaseDialect dialect, int index) {
        String raw = "CHK_" + table.qualifiedName().name().value() + "_" + index;
        String normalized = dialect.identifierPolicy().normalize(raw)
                .replaceAll("[^A-Z0-9_$#]", "_")
                .toUpperCase(Locale.ROOT);
        int maximumLength = dialect.identifierPolicy().maximumLength();
        return Identifier.of(normalized.length() <= maximumLength
                ? normalized
                : normalized.substring(0, maximumLength));
    }
}
