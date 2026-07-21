package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Synonym;
import com.behsazan.schemaforge.generation.model.SqlSection;
import com.behsazan.schemaforge.generation.model.SqlStatement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Generates private and public Oracle synonyms after views. */
/**
 * @deprecated since 3.3. Replaced by {@link com.behsazan.schemaforge.generation.ddl.generator.schema.SchemaScriptGenerator} or the vendor-neutral DDL pipeline.
 * Scheduled for removal in Phase 3.6.
 */
@Deprecated(forRemoval = true, since = "3.3")
public final class OracleSynonymGenerator {

    public SqlSection generate(DatabaseSchema schema) {
        List<SqlStatement> statements = new ArrayList<>();

        for (Synonym synonym : schema.synonyms().stream()
                .sorted(Comparator.comparing(item -> item.qualifiedName().toString()))
                .toList()) {
            statements.add(new SqlStatement(
                    buildSynonym(synonym),
                    synonym.publicSynonym() ? "PUBLIC_SYNONYM" : "SYNONYM",
                    synonym.qualifiedName().toString(),
                    statements.size()));
        }

        return new SqlSection("Synonyms", 190, statements);
    }

    private String buildSynonym(Synonym synonym) {
        StringBuilder sql = new StringBuilder("CREATE OR REPLACE ");
        if (synonym.publicSynonym()) {
            sql.append("PUBLIC SYNONYM ").append(synonym.qualifiedName().name());
        } else {
            sql.append("SYNONYM ").append(synonym.qualifiedName());
        }
        return sql.append(" FOR ").append(synonym.target()).append(';').toString();
    }
}
