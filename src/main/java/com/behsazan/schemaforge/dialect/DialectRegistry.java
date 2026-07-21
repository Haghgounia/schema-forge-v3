package com.behsazan.schemaforge.dialect;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class DialectRegistry {

    private final Map<DatabaseProduct, DatabaseDialect> dialects;

    public DialectRegistry(Collection<? extends DatabaseDialect> dialects) {
        Objects.requireNonNull(dialects, "dialects must not be null");
        Map<DatabaseProduct, DatabaseDialect> values = new EnumMap<>(DatabaseProduct.class);
        for (DatabaseDialect dialect : dialects) {
            DatabaseDialect previous = values.put(
                    Objects.requireNonNull(dialect, "dialect must not be null").product(),
                    dialect);
            if (previous != null) {
                throw new IllegalArgumentException("duplicate dialect: " + dialect.product());
            }
        }
        this.dialects = Map.copyOf(values);
    }

    public DatabaseDialect require(DatabaseProduct product) {
        DatabaseDialect dialect = dialects.get(Objects.requireNonNull(product, "product must not be null"));
        if (dialect == null) {
            throw new IllegalArgumentException("dialect is not registered: " + product);
        }
        return dialect;
    }

    public boolean contains(DatabaseProduct product) {
        return dialects.containsKey(product);
    }
}
