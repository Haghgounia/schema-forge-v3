package com.behsazan.schemaforge.generation.ddl.generator.sequence;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.domain.model.Sequence;
import com.behsazan.schemaforge.generation.ddl.generator.table.IdentifierSqlRenderer;
import com.behsazan.schemaforge.generation.ddl.model.*;
import java.util.Objects;

public final class SequenceGenerator {
    private final IdentifierSqlRenderer identifiers = new IdentifierSqlRenderer();

    public DdlStatement generate(Sequence sequence, DatabaseDialect dialect, int position) {
        Objects.requireNonNull(sequence); Objects.requireNonNull(dialect);
        StringBuilder sql = new StringBuilder("CREATE SEQUENCE ")
                .append(identifiers.render(sequence.qualifiedName(), dialect))
                .append(" START WITH ").append(sequence.startWith())
                .append(" INCREMENT BY ").append(sequence.incrementBy());
        boolean postgresql = dialect.product() == DatabaseProduct.POSTGRESQL;
        if (sequence.minValue() != null) sql.append(" MINVALUE ").append(sequence.minValue());
        else sql.append(postgresql ? " NO MINVALUE" : " NOMINVALUE");
        if (sequence.maxValue() != null) sql.append(" MAXVALUE ").append(sequence.maxValue());
        else sql.append(postgresql ? " NO MAXVALUE" : " NOMAXVALUE");
        sql.append(sequence.cycle() ? " CYCLE" : (postgresql ? " NO CYCLE" : " NOCYCLE"));
        if (sequence.cacheSize() == null || sequence.cacheSize() == 0) {
            if (!postgresql) sql.append(" NOCACHE");
        } else sql.append(" CACHE ").append(sequence.cacheSize());
        return DdlStatement.of(DdlStatementType.CREATE_SEQUENCE,
                new DdlObjectReference(sequence.qualifiedName().schemaName().map(Object::toString).orElse(""),
                        sequence.qualifiedName().name().toString(), "SEQUENCE"),
                new StatementOrder(DdlPhase.SEQUENCES, position), SqlFragment.of(sql.toString()));
    }
}
