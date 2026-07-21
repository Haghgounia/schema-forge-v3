package com.behsazan.schemaforge.dialect.standard;

import com.behsazan.schemaforge.dialect.AbstractDatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseProduct;

public final class StandardDialect extends AbstractDatabaseDialect {

    public StandardDialect() {
        super(new StandardIdentifierRules(), new StandardDataTypeRules(), new StandardDdlSyntax());
    }

    @Override
    public DatabaseProduct product() {
        return DatabaseProduct.STANDARD;
    }

    @Override
    public String name() {
        return "SQL Standard";
    }
}
