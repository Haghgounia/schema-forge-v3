package com.behsazan.schemaforge.generation.ddl.generator.constraint;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.generation.ddl.generator.table.IdentifierSqlRenderer;
import com.behsazan.schemaforge.generation.ddl.model.DdlObjectReference;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ConstraintSqlSupport {
    private final IdentifierSqlRenderer identifierRenderer = new IdentifierSqlRenderer();

    public String tableName(Table table, DatabaseDialect dialect) {
        return identifierRenderer.render(table.qualifiedName(), dialect);
    }

    public String identifier(Identifier identifier, DatabaseDialect dialect) {
        return identifierRenderer.render(identifier, dialect);
    }

    public String columns(List<Identifier> columns, DatabaseDialect dialect) {
        Objects.requireNonNull(columns, "columns must not be null");
        return columns.stream()
                .map(column -> identifier(column, dialect))
                .collect(Collectors.joining(", "));
    }

    public String oracleUsingIndex(Table table, Identifier constraintName, List<Identifier> columns, DatabaseDialect dialect) {
        if (dialect.product() != DatabaseProduct.ORACLE) {
            return "";
        }
        String schema = table.qualifiedName().schemaName().map(value -> value.value()).orElse("");
        String qualifiedIndexName = schema.isBlank()
                ? identifier(constraintName, dialect)
                : identifierRenderer.render(table.qualifiedName().schema(), dialect) + "." + identifier(constraintName, dialect);
        String tablespace = schema.isBlank() ? "ITS" : "ITS_" + schema;
        return "\nUSING INDEX (CREATE UNIQUE INDEX " + qualifiedIndexName
                + " ON " + tableName(table, dialect)
                + " (" + columns(columns, dialect) + ")"
                + " TABLESPACE " + tablespace + ")";
    }

    public DdlObjectReference reference(Table table, String objectType) {
        return new DdlObjectReference(
                table.qualifiedName().schemaName().map(Object::toString).orElse(""),
                table.qualifiedName().name().toString(),
                objectType);
    }
}
