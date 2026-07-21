package com.behsazan.schemaforge.generation.ddl.generator.sequence;

import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.domain.model.Sequence;
import com.behsazan.schemaforge.domain.valueobject.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SequenceGeneratorTest {
    @Test void generatesOracleSequenceOptions() {
        Sequence sequence = new Sequence(QualifiedName.of("CRM", "SEQ_CUSTOMER"), 100, 5, 1L, 9999L, true, 20, Description.empty());
        String ddl = new SequenceGenerator().generate(sequence, new OracleDialect(), 0).fragments().getFirst().value();
        assertEquals("CREATE SEQUENCE CRM.SEQ_CUSTOMER START WITH 100 INCREMENT BY 5 MINVALUE 1 MAXVALUE 9999 CYCLE CACHE 20", ddl);
    }
}
