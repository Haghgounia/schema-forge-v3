package com.behsazan.schemaforge.generation.core;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.generation.ddl.model.DdlScript;
import com.behsazan.schemaforge.generation.ddl.renderer.RenderedDdl;
import java.util.Objects;

/** Result of generating and rendering one database-specific DDL script. */
public record DdlGenerationResult(
        DatabaseProduct databaseProduct,
        DdlScript script,
        RenderedDdl renderedDdl) {

    public DdlGenerationResult {
        Objects.requireNonNull(databaseProduct, "databaseProduct must not be null");
        Objects.requireNonNull(script, "script must not be null");
        Objects.requireNonNull(renderedDdl, "renderedDdl must not be null");
    }

    public String ddl() {
        return renderedDdl.content();
    }

    public int statementCount() {
        return renderedDdl.statementCount();
    }
}
