package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Sequence;
import com.behsazan.schemaforge.generation.model.SqlSection;
import com.behsazan.schemaforge.generation.model.SqlStatement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Generates Oracle sequence DDL in the legacy SchemaForge organizational format.
 * The golden Oracle dataset uses NOCACHE ORDER unless a positive cache size is supplied.
 */
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
                .append(" INCREMENT BY ").append(sequence.incrementBy());

        if (sequence.maxValue() != null) {
            sql.append(" MAXVALUE ").append(sequence.maxValue());
        } else {
            sql.append(" NOMAXVALUE");
        }

        if (sequence.minValue() != null) {
            sql.append(" MINVALUE ").append(sequence.minValue());
        } else {
            sql.append(" NOMINVALUE");
        }

        if (sequence.cacheSize() == null || sequence.cacheSize() == 0) {
            sql.append(" NOCACHE");
        } else {
            sql.append(" CACHE ").append(sequence.cacheSize());
        }

        sql.append(" ORDER");
        if (sequence.cycle()) sql.append(" CYCLE");
        sql.append(';');
        return sql.toString();
    }
}
