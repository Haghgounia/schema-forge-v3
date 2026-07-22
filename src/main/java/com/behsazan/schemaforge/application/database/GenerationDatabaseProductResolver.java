package com.behsazan.schemaforge.application.database;

import com.behsazan.schemaforge.configuration.properties.SchemaForgeProperties;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import org.springframework.stereotype.Component;

/** Resolves the configured target database without leaking configuration into use cases. */
@Component
public final class GenerationDatabaseProductResolver {
    private final SchemaForgeProperties properties;

    public GenerationDatabaseProductResolver(SchemaForgeProperties properties) {
        this.properties = properties;
    }

    public DatabaseProduct resolve() {
        DatabaseType type = properties.generation().defaultDatabase();
        return switch (type) {
            case ORACLE -> DatabaseProduct.ORACLE;
            case POSTGRESQL -> DatabaseProduct.POSTGRESQL;
            default -> throw new IllegalArgumentException(
                    "DDL/compare generation is not registered for configured database: " + type);
        };
    }
}
