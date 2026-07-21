package com.behsazan.schemaforge.dialect.postgresql;

import com.behsazan.schemaforge.dialect.*;

public final class PostgreSqlDialect extends AbstractDatabaseDialect {
    public PostgreSqlDialect() { this(new PostgreSqlIdentifierPolicy(), new PostgreSqlReservedWordProvider()); }
    private PostgreSqlDialect(PostgreSqlIdentifierPolicy policy, PostgreSqlReservedWordProvider words) {
        super(new PostgreSqlIdentifierRules(), new PostgreSqlDataTypeRules(), new PostgreSqlDdlSyntax(),
                new PostgreSqlDdlGenerationPolicy(),
                DatabaseCapabilities.of(DatabaseCapability.SEQUENCE, DatabaseCapability.IDENTITY,
                        DatabaseCapability.CHECK_CONSTRAINT, DatabaseCapability.COMMENT_ON,
                        DatabaseCapability.GENERATED_COLUMN, DatabaseCapability.CASCADE_DELETE,
                        DatabaseCapability.DEFERRABLE_CONSTRAINT, DatabaseCapability.TABLESPACE),
                policy, words, new PostgreSqlNamingStrategy(policy), new PostgreSqlSqlTypeMapper());
    }
    @Override public DatabaseProduct product() { return DatabaseProduct.POSTGRESQL; }
    @Override public String name() { return "PostgreSQL"; }
}
