package com.behsazan.schemaforge.dialect.oracle;

import com.behsazan.schemaforge.dialect.AbstractDatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseProduct;

public final class OracleDialect extends AbstractDatabaseDialect {

    public OracleDialect() {
        super(new OracleIdentifierRules(), new OracleDataTypeRules(), new OracleDdlSyntax());
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
