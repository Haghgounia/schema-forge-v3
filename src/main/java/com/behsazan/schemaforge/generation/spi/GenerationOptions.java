package com.behsazan.schemaforge.generation.spi;

import java.util.Map;

public record GenerationOptions(
        boolean includeDropStatements,
        boolean includeRollback,
        boolean includeComments,
        boolean includeDbaHints,
        Map<String, Object> providerOptions) {
    public GenerationOptions {
        providerOptions = providerOptions == null ? Map.of() : Map.copyOf(providerOptions);
    }

    public static GenerationOptions defaults() {
        return new GenerationOptions(false, true, true, true, Map.of());
    }
}
