package com.behsazan.schemaforge.validation.core;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DialectDefaults;

import java.util.Objects;

/** Resolves the database dialect used during one validation execution. */
public final class ValidationDialectResolver {

    public static final String ATTRIBUTE_DIALECT = "databaseDialect";

    private ValidationDialectResolver() {
    }

    public static DatabaseDialect resolve(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");
        DatabaseDialect dialect = context.get(ATTRIBUTE_DIALECT);
        return dialect == null ? DialectDefaults.defaultDialect() : dialect;
    }

    public static void use(ValidationContext context, DatabaseDialect dialect) {
        Objects.requireNonNull(context, "context must not be null");
        context.put(ATTRIBUTE_DIALECT, Objects.requireNonNull(dialect, "dialect must not be null"));
    }
}
