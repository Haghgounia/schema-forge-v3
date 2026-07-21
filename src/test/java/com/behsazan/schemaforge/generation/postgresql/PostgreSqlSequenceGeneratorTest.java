package com.behsazan.schemaforge.generation.postgresql;

import com.behsazan.schemaforge.dialect.postgresql.PostgreSqlDialect;
import com.behsazan.schemaforge.domain.model.Sequence;
import com.behsazan.schemaforge.domain.valueobject.QualifiedName;
import com.behsazan.schemaforge.generation.ddl.generator.sequence.SequenceGenerator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PostgreSqlSequenceGeneratorTest {
    @Test void usesPostgreSqlSequenceKeywords() {
        Sequence sequence = new Sequence(QualifiedName.of("SALES", "SEQ_ORDER"), 1L, 1L,
                null, null, false, 20, null);
        String sql = new SequenceGenerator().generate(sequence, new PostgreSqlDialect(), 0)
                .fragments().getFirst().value();
        assertTrue(sql.contains("CREATE SEQUENCE sales.seq_order"));
        assertTrue(sql.contains("NO MINVALUE"));
        assertTrue(sql.contains("NO MAXVALUE"));
        assertTrue(sql.contains("NO CYCLE"));
        assertTrue(sql.contains("CACHE 20"));
        assertFalse(sql.contains("NOMINVALUE"));
    }
}
