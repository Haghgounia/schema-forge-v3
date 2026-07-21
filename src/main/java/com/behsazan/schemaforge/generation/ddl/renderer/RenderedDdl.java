package com.behsazan.schemaforge.generation.ddl.renderer;

import java.util.Objects;

public record RenderedDdl(String content, int statementCount) {
    public RenderedDdl {
        content = Objects.requireNonNull(content, "content must not be null");
        if (statementCount < 0) {
            throw new IllegalArgumentException("statementCount must not be negative");
        }
    }

    public boolean isEmpty() {
        return content.isBlank();
    }
}
