package com.behsazan.schemaforge.generation.core;

import com.behsazan.schemaforge.generation.spi.DatabaseDialect;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @deprecated since 3.3. Replaced by {@link com.behsazan.schemaforge.dialect.DialectRegistry}.
 * Scheduled for removal in Phase 3.6.
 */
@Deprecated(forRemoval = true, since = "3.3")
public final class DialectRegistry {
    private final Map<DatabaseType, DatabaseDialect> dialects;

    public DialectRegistry(List<DatabaseDialect> dialects) {
        EnumMap<DatabaseType, DatabaseDialect> index = new EnumMap<>(DatabaseType.class);
        for (DatabaseDialect dialect : dialects) {
            DatabaseDialect previous = index.put(dialect.type(), dialect);
            if (previous != null) {
                throw new IllegalArgumentException("Duplicate dialect: " + dialect.type());
            }
        }
        this.dialects = Map.copyOf(index);
    }

    public List<DatabaseDialect> all() {
        return dialects.values().stream().toList();
    }

    public DatabaseDialect require(DatabaseType type) {
        Objects.requireNonNull(type, "type must not be null");
        DatabaseDialect dialect = dialects.get(type);
        if (dialect == null) {
            throw new IllegalArgumentException("No dialect registered for " + type);
        }
        return dialect;
    }
}
