package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Sequence;
import com.behsazan.schemaforge.generation.model.SqlSection;
import com.behsazan.schemaforge.generation.model.SqlStatement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Generates Oracle sequence DDL in the legacy SchemaForge format.
 * Sequences are emitted before tables so NEXTVAL defaults are valid during installation.
 */
/**
 * @deprecated since 3.3. Replaced by {@link com.behsazan.schemaforge.generation.ddl.generator.sequence.SequenceGenerator} or the vendor-neutral DDL pipeline.
 * Scheduled for removal in Phase 3.6.
 */
@Deprecated(forRemoval = true, since = "3.3")
public final class OracleSequenceGenerator {

    public SqlSection generate(DatabaseSchema schema) {
        List<SqlStatement> statements = new ArrayList<>();
        int order = 0;

        for (Sequence sequence : schema.sequences().stream()
                .sorted(Comparator.comparing(item -> item.qualifiedName().toString()))
                .toList()) {
            statements.add(new SqlStatement(
                    buildSequence(sequence),
                    "SEQUENCE",
                    sequence.qualifiedName().toString(),
                    order++));
        }

        return new SqlSection("Sequences", 50, statements);
    }

    private String buildSequence(Sequence sequence) {
        StringBuilder sql = new StringBuilder("CREATE SEQUENCE ")
                .append(sequence.qualifiedName())
                .append("\n    INCREMENT BY ").append(sequence.incrementBy())
                .append("\n    START WITH ").append(sequence.startWith());

        if (sequence.maxValue() != null) {
            sql.append("\n    MAXVALUE ").append(sequence.maxValue());
        } else {
            sql.append("\n    NOMAXVALUE");
        }

        if (sequence.minValue() != null) {
            sql.append("\n    MINVALUE ").append(sequence.minValue());
        } else {
            sql.append("\n    NOMINVALUE");
        }

        if (sequence.cacheSize() == null || sequence.cacheSize() <= 0) {
            sql.append("\n    NOCACHE");
        } else {
            sql.append("\n    CACHE ").append(sequence.cacheSize());
        }

        sql.append("\n    ORDER");
        if (sequence.cycle()) {
            sql.append("\n    CYCLE");
        }
        sql.append(';');

        return sql.toString();
    }
}
