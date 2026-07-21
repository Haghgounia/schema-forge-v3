package com.behsazan.schemaforge.generation.ddl.model;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import java.time.Clock;
import java.util.Map;
import java.util.Objects;

public record RenderContext(
        DatabaseDialect dialect,
        ScriptOptions options,
        Clock clock,
        Map<String, Object> attributes) {

    public RenderContext {
        Objects.requireNonNull(dialect, "dialect must not be null");
        options = options == null ? ScriptOptions.defaults() : options;
        clock = clock == null ? Clock.systemUTC() : clock;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
