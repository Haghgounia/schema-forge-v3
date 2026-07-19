package com.behsazan.schemaforge.generation.spi;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import java.time.Clock;
import java.util.Objects;

public record GenerationContext(
        DatabaseSchema schema,
        DatabaseType databaseType,
        GenerationOptions options,
        Clock clock) {
    public GenerationContext {
        Objects.requireNonNull(schema, "schema must not be null");
        Objects.requireNonNull(databaseType, "databaseType must not be null");
        options = options == null ? GenerationOptions.defaults() : options;
        clock = clock == null ? Clock.systemUTC() : clock;
    }
}
