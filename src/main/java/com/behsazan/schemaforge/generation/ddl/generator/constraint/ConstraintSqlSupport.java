package com.behsazan.schemaforge.generation.ddl.generator.constraint;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.generation.ddl.generator.table.IdentifierSqlRenderer;
import com.behsazan.schemaforge.generation.ddl.model.DdlObjectReference;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class ConstraintSqlSupport {
    private final IdentifierSqlRenderer identifierRenderer = new IdentifierSqlRenderer();

    String tableName(Table table, DatabaseDialect dialect) {
        return identifierRenderer.render(table.qualifiedName(), dialect);
    }

    String identifier(Identifier identifier, DatabaseDialect dialect) {
        return identifierRenderer.render(identifier, dialect);
    }

    String columns(List<Identifier> columns, DatabaseDialect dialect) {
        Objects.requireNonNull(columns, "columns must not be null");
        return columns.stream()
                .map(column -> identifier(column, dialect))
                .collect(Collectors.joining(", "));
    }

    DdlObjectReference reference(Table table, String objectType) {
        return new DdlObjectReference(
                table.qualifiedName().schemaName().map(Object::toString).orElse(""),
                table.qualifiedName().name().toString(),
                objectType);
    }
}
