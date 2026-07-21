package com.behsazan.schemaforge.generation.ddl.generator.table;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.domain.valueobject.QualifiedName;
import java.util.Objects;

public final class IdentifierSqlRenderer {

    public String render(Identifier identifier, DatabaseDialect dialect) {
        Objects.requireNonNull(identifier, "identifier must not be null");
        Objects.requireNonNull(dialect, "dialect must not be null");
        String value = identifier.value();
        if (dialect.identifierPolicy().isValidUnquoted(value)
                && !dialect.reservedWordProvider().isReserved(value)) {
            return dialect.identifierPolicy().normalize(value);
        }
        return dialect.identifierPolicy().quote(value);
    }

    public String render(QualifiedName name, DatabaseDialect dialect) {
        Objects.requireNonNull(name, "name must not be null");
        String objectName = render(name.name(), dialect);
        return name.schemaName()
                .map(schema -> render(schema, dialect) + "." + objectName)
                .orElse(objectName);
    }
}
