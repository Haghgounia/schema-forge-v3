package com.behsazan.schemaforge.generation.core;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.generation.ddl.model.ScriptOptions;
import java.time.Clock;
import java.util.Map;
import java.util.Objects;

/** Immutable input for vendor-neutral DDL generation. */
public record DdlGenerationRequest(
        DatabaseSchema schema,
        DatabaseProduct databaseProduct,
        ScriptOptions options,
        Clock clock,
        Map<String, Object> attributes) {

    public DdlGenerationRequest {
        Objects.requireNonNull(schema, "schema must not be null");
        Objects.requireNonNull(databaseProduct, "databaseProduct must not be null");
        options = options == null ? ScriptOptions.defaults() : options;
        clock = clock == null ? Clock.systemUTC() : clock;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static DdlGenerationRequest of(
            DatabaseSchema schema,
            DatabaseProduct databaseProduct,
            ScriptOptions options,
            Clock clock) {
        return new DdlGenerationRequest(schema, databaseProduct, options, clock, Map.of());
    }
}
