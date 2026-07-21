package com.behsazan.schemaforge.dialect.oracle;

import com.behsazan.schemaforge.dialect.AbstractDatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseCapabilities;
import com.behsazan.schemaforge.dialect.DatabaseCapability;
import com.behsazan.schemaforge.dialect.DatabaseProduct;

public final class OracleDialect extends AbstractDatabaseDialect {

    public OracleDialect() {
        this(new OracleIdentifierPolicy(), new OracleReservedWordProvider());
    }

    private OracleDialect(
            OracleIdentifierPolicy identifierPolicy,
            OracleReservedWordProvider reservedWordProvider) {
        super(
                new OracleIdentifierRules(),
                new OracleDataTypeRules(),
                new OracleDdlSyntax(),
                new OracleDdlGenerationPolicy(),
                DatabaseCapabilities.of(
                        DatabaseCapability.SEQUENCE,
                        DatabaseCapability.IDENTITY,
                        DatabaseCapability.CHECK_CONSTRAINT,
                        DatabaseCapability.COMMENT_ON,
                        DatabaseCapability.SYNONYM,
                        DatabaseCapability.MATERIALIZED_VIEW,
                        DatabaseCapability.GENERATED_COLUMN,
                        DatabaseCapability.CASCADE_DELETE,
                        DatabaseCapability.DEFERRABLE_CONSTRAINT,
                        DatabaseCapability.TABLESPACE),
                identifierPolicy,
                reservedWordProvider,
                new OracleNamingStrategy(identifierPolicy),
                new OracleSqlTypeMapper());
    }

    @Override
    public DatabaseProduct product() {
        return DatabaseProduct.ORACLE;
    }

    @Override
    public String name() {
        return "Oracle Database";
    }
}
