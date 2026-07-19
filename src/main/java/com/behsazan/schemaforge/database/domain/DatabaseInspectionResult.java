package com.behsazan.schemaforge.database.domain;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record DatabaseInspectionResult(
        DatabaseSchema schema,
        Instant inspectedAt,
        List<String> warnings) {
    public DatabaseInspectionResult {
        Objects.requireNonNull(schema, "schema must not be null");
        inspectedAt = inspectedAt == null ? Instant.now() : inspectedAt;
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }
}
