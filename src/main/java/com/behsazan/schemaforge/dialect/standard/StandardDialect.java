package com.behsazan.schemaforge.dialect.standard;

import com.behsazan.schemaforge.dialect.AbstractDatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseCapabilities;
import com.behsazan.schemaforge.dialect.DatabaseCapability;
import com.behsazan.schemaforge.dialect.DatabaseProduct;

public final class StandardDialect extends AbstractDatabaseDialect {

    public StandardDialect() {
        this(new StandardIdentifierPolicy(), new StandardReservedWordProvider());
    }

    private StandardDialect(
            StandardIdentifierPolicy identifierPolicy,
            StandardReservedWordProvider reservedWordProvider) {
        super(
                new StandardIdentifierRules(),
                new StandardDataTypeRules(),
                new StandardDdlSyntax(),
                DatabaseCapabilities.of(
                        DatabaseCapability.IDENTITY,
                        DatabaseCapability.CHECK_CONSTRAINT,
                        DatabaseCapability.COMMENT_ON,
                        DatabaseCapability.CASCADE_DELETE),
                identifierPolicy,
                reservedWordProvider,
                new StandardNamingStrategy(identifierPolicy),
                new StandardSqlTypeMapper());
    }

    @Override
    public DatabaseProduct product() {
        return DatabaseProduct.STANDARD;
    }

    @Override
    public String name() {
        return "Standard SQL";
    }
}
