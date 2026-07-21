package com.behsazan.schemaforge.generation.ddl.generator.comment;

import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommentGeneratorTest {
    @Test void generatesTableAndColumnCommentsAndEscapesQuotes() {
        Column c = new Column(Identifier.of("NAME"), DataType.varchar("VARCHAR2", 50), false,
                null, new Description("Customer's name"), false, 1);
        Table t = Table.builder("CRM", "CUSTOMER").description("Customer master").addColumn(c).build();
        var statements = new CommentGenerator().generate(t, new OracleDialect(), 0);
        assertEquals(2, statements.size());
        assertTrue(statements.get(0).fragments().getFirst().value().contains("COMMENT ON TABLE CRM.CUSTOMER IS 'Customer master'"));
        assertTrue(statements.get(1).fragments().getFirst().value().contains("Customer''s name"));
    }
}
