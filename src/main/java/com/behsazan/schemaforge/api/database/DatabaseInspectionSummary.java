package com.behsazan.schemaforge.api.database;

import com.behsazan.schemaforge.database.domain.DatabaseInspectionResult;
import java.time.Instant;
import java.util.List;

public record DatabaseInspectionSummary(
        String schema,
        Instant inspectedAt,
        int tables,
        int sequences,
        int views,
        int materializedViews,
        int synonyms,
        int triggers,
        int routines,
        List<String> warnings) {

    public static DatabaseInspectionSummary from(DatabaseInspectionResult result) {
        var schema = result.schema();
        int materializedViews = (int) schema.views().stream().filter(view -> view.materialized()).count();
        return new DatabaseInspectionSummary(
                schema.name().value(),
                result.inspectedAt(),
                schema.tables().size(),
                schema.sequences().size(),
                schema.views().size() - materializedViews,
                materializedViews,
                schema.synonyms().size(),
                schema.triggers().size(),
                schema.routines().size(),
                result.warnings());
    }
}
