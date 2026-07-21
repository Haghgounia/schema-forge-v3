package com.behsazan.schemaforge.generation.ddl.generator.index;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.domain.enums.IndexType;
import com.behsazan.schemaforge.domain.enums.SortDirection;
import com.behsazan.schemaforge.domain.model.Index;
import com.behsazan.schemaforge.domain.model.IndexColumn;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.generation.ddl.generator.table.IdentifierSqlRenderer;
import com.behsazan.schemaforge.generation.ddl.model.DdlObjectReference;
import com.behsazan.schemaforge.generation.ddl.model.DdlPhase;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatement;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatementType;
import com.behsazan.schemaforge.generation.ddl.model.SqlFragment;
import com.behsazan.schemaforge.generation.ddl.model.StatementOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Generates standalone indexes after table constraints. */
public final class IndexGenerator {
    private final IdentifierSqlRenderer identifiers = new IdentifierSqlRenderer();

    public List<DdlStatement> generate(Table table, DatabaseDialect dialect, int startingPosition) {
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(dialect, "dialect must not be null");
        if (startingPosition < 0) {
            throw new IllegalArgumentException("startingPosition must not be negative");
        }

        List<DdlStatement> statements = new ArrayList<>();
        for (int index = 0; index < table.indexes().size(); index++) {
            statements.add(statement(table, table.indexes().get(index), dialect, startingPosition + index));
        }
        return List.copyOf(statements);
    }

    private DdlStatement statement(Table table, Index index, DatabaseDialect dialect, int position) {
        Identifier name = index.name() == null
                ? Identifier.of(dialect.namingStrategy().index(
                        table.qualifiedName().name().value(),
                        index.columns().stream().map(column -> column.column().value()).collect(Collectors.joining("_"))))
                : index.name();

        String ddl = "CREATE " + typePrefix(index.type(), dialect)
                + "INDEX " + qualifiedIndexName(table, name, dialect)
                + "\nON " + identifiers.render(table.qualifiedName(), dialect)
                + " (" + columns(index.columns(), dialect) + ")";

        return DdlStatement.of(
                DdlStatementType.CREATE_INDEX,
                new DdlObjectReference(
                        table.qualifiedName().schemaName().map(Object::toString).orElse(""),
                        name.value(),
                        "INDEX"),
                new StatementOrder(DdlPhase.INDEXES, position),
                SqlFragment.of(ddl));
    }

    private String qualifiedIndexName(Table table, Identifier name, DatabaseDialect dialect) {
        String renderedName = identifiers.render(name, dialect);
        if (dialect.product() == DatabaseProduct.POSTGRESQL || table.qualifiedName().schema() == null) {
            return renderedName;
        }
        return identifiers.render(table.qualifiedName().schema(), dialect) + "." + renderedName;
    }

    private String columns(List<IndexColumn> columns, DatabaseDialect dialect) {
        return columns.stream()
                .map(column -> identifiers.render(column.column(), dialect)
                        + (column.direction() == SortDirection.DESC ? " DESC" : " ASC"))
                .collect(Collectors.joining(", "));
    }

    private static String typePrefix(IndexType type, DatabaseDialect dialect) {
        if (dialect.product() == DatabaseProduct.ORACLE) {
            return switch (type) {
                case NORMAL -> "";
                case UNIQUE -> "UNIQUE ";
                case BITMAP -> "BITMAP ";
                case FUNCTION_BASED, CLUSTERED, NONCLUSTERED -> throw new IllegalArgumentException(
                        "Oracle index type is not supported by Index Engine v1: " + type);
            };
        }
        return switch (type) {
            case NORMAL -> "";
            case UNIQUE -> "UNIQUE ";
            case BITMAP, FUNCTION_BASED, CLUSTERED, NONCLUSTERED -> throw new IllegalArgumentException(
                    "Index type is not supported by the selected dialect in Index Engine v1: " + type);
        };
    }
}
