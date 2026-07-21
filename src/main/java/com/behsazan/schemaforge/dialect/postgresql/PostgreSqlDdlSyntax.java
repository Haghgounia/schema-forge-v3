package com.behsazan.schemaforge.dialect.postgresql;

import com.behsazan.schemaforge.dialect.DdlSyntax;
import java.util.Objects;

public final class PostgreSqlDdlSyntax implements DdlSyntax {
    @Override public String statementTerminator() { return ";"; }
    @Override public String quoteIdentifier(String identifier) {
        Objects.requireNonNull(identifier, "identifier must not be null");
        return '"' + identifier.replace("\"", "\"\"") + '"';
    }
    @Override public String currentTimestampExpression() { return "CURRENT_TIMESTAMP"; }
    @Override public boolean supportsCreateSequence() { return true; }
    @Override public boolean supportsComments() { return true; }
}
