package com.behsazan.schemaforge.dialect;

import com.behsazan.schemaforge.dialect.oracle.OracleDialect;

/** Provides the application default dialect without exposing vendor classes to consumers. */
public final class DialectDefaults {

    private static final DatabaseDialect DEFAULT_DIALECT = new OracleDialect();

    private DialectDefaults() {
    }

    public static DatabaseDialect defaultDialect() {
        return DEFAULT_DIALECT;
    }
}
