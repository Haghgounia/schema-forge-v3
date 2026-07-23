package com.behsazan.schemaforge.generation.ddl.generator.constraint;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.model.PrimaryKey;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.generation.ddl.model.DdlPhase;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatement;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatementType;
import com.behsazan.schemaforge.generation.ddl.model.SqlFragment;
import com.behsazan.schemaforge.generation.ddl.model.StatementOrder;
import java.util.Objects;
import java.util.Optional;

public final class PrimaryKeyGenerator {
    private final ConstraintSqlSupport sql = new ConstraintSqlSupport();

    public Optional<DdlStatement> generate(Table table, DatabaseDialect dialect, int position) {
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(dialect, "dialect must not be null");
        if (position < 0) {
            throw new IllegalArgumentException("position must not be negative");
        }
        return table.primaryKey().map(primaryKey -> statement(table, primaryKey, dialect, position));
    }

    private DdlStatement statement(Table table, PrimaryKey primaryKey, DatabaseDialect dialect, int position) {
        Identifier name = primaryKey.name() == null
                ? Identifier.of(dialect.namingStrategy().primaryKey(table.qualifiedName().name().value()))
                : primaryKey.name();
        String ddl = "ALTER TABLE " + sql.tableName(table, dialect)
                + "\nADD CONSTRAINT " + sql.identifier(name, dialect)
                + "\nPRIMARY KEY (" + sql.columns(primaryKey.columns(), dialect) + ")"
                + sql.oracleUsingIndex(table, name, primaryKey.columns(), dialect);
        return DdlStatement.of(
                DdlStatementType.CREATE_PRIMARY_KEY,
                sql.reference(table, "PRIMARY_KEY"),
                new StatementOrder(DdlPhase.PRIMARY_KEYS, position),
                SqlFragment.of(ddl));
    }
}
