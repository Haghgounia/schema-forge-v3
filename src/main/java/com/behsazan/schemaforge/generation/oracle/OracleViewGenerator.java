package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.View;
import com.behsazan.schemaforge.generation.model.SqlSection;
import com.behsazan.schemaforge.generation.model.SqlStatement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Generates Oracle views and materialized views after tables and indexes. */
public final class OracleViewGenerator {

    public List<SqlSection> generate(DatabaseSchema schema, boolean includeComments) {
        List<SqlStatement> views = new ArrayList<>();
        List<SqlStatement> materializedViews = new ArrayList<>();
        List<SqlStatement> comments = new ArrayList<>();

        for (View view : schema.views().stream()
                .sorted(Comparator.comparing(item -> item.qualifiedName().toString()))
                .toList()) {
            if (view.materialized()) {
                materializedViews.add(new SqlStatement(
                        buildMaterializedView(view),
                        "MATERIALIZED_VIEW",
                        view.qualifiedName().toString(),
                        materializedViews.size()));
            } else {
                views.add(new SqlStatement(
                        buildView(view),
                        "VIEW",
                        view.qualifiedName().toString(),
                        views.size()));
            }

            if (includeComments && !view.description().isEmpty()) {
                comments.add(new SqlStatement(
                        "COMMENT ON TABLE " + view.qualifiedName() + " IS '"
                                + escape(view.description().value()) + "';",
                        "VIEW_COMMENT",
                        view.qualifiedName().toString(),
                        comments.size()));
            }
        }

        List<SqlSection> sections = new ArrayList<>();
        sections.add(new SqlSection("Views", 170, views));
        sections.add(new SqlSection("Materialized Views", 180, materializedViews));
        sections.add(new SqlSection("View Comments", 210, comments));
        return sections;
    }

    private String buildView(View view) {
        return "CREATE OR REPLACE VIEW " + view.qualifiedName() + " AS\n" + normalizeQuery(view.query()) + ";";
    }

    private String buildMaterializedView(View view) {
        return "CREATE MATERIALIZED VIEW " + view.qualifiedName() + "\nAS\n" + normalizeQuery(view.query()) + ";";
    }

    private String normalizeQuery(String query) {
        String normalized = query.strip();
        while (normalized.endsWith(";")) {
            normalized = normalized.substring(0, normalized.length() - 1).stripTrailing();
        }
        return normalized;
    }

    private String escape(String value) {
        return value.replace("'", "''");
    }
}
