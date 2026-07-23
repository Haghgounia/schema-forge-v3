package com.behsazan.schemaforge.generation.ddl.generator.constraint;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.model.UniqueKey;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.generation.ddl.model.DdlPhase;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatement;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatementType;
import com.behsazan.schemaforge.generation.ddl.model.SqlFragment;
import com.behsazan.schemaforge.generation.ddl.model.StatementOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class UniqueKeyGenerator {
    private final ConstraintSqlSupport sql = new ConstraintSqlSupport();

    public List<DdlStatement> generate(Table table, DatabaseDialect dialect, int startingPosition) {
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(dialect, "dialect must not be null");
        if (startingPosition < 0) {
            throw new IllegalArgumentException("startingPosition must not be negative");
        }
        List<DdlStatement> statements = new ArrayList<>();
        for (int index = 0; index < table.uniqueKeys().size(); index++) {
            UniqueKey uniqueKey = table.uniqueKeys().get(index);
            Identifier name = uniqueKey.name() == null
                    ? Identifier.of(dialect.namingStrategy().uniqueKey(
                            table.qualifiedName().name().value(), Integer.toString(index + 1)))
                    : uniqueKey.name();
            String ddl = "ALTER TABLE " + sql.tableName(table, dialect)
                    + "\nADD CONSTRAINT " + sql.identifier(name, dialect)
                    + "\nUNIQUE (" + sql.columns(uniqueKey.columns(), dialect) + ")"
                    + sql.oracleUsingIndex(table, name, uniqueKey.columns(), dialect);
            statements.add(DdlStatement.of(
                    DdlStatementType.CREATE_UNIQUE_KEY,
                    sql.reference(table, "UNIQUE_KEY"),
                    new StatementOrder(DdlPhase.UNIQUE_KEYS, startingPosition + index),
                    SqlFragment.of(ddl)));
        }
        return List.copyOf(statements);
    }
}
