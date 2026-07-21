package com.behsazan.schemaforge.generation.ddl.generator.grant;

import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.domain.model.Grant;
import com.behsazan.schemaforge.domain.valueobject.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GrantGeneratorTest {
    @Test void generatesObjectGrantWithGrantOption() {
        Grant grant = new Grant(QualifiedName.of("CRM", "CUSTOMER"), "TABLE", Identifier.of("API_USER"), List.of("select", "update"), true);
        String ddl = new GrantGenerator().generate(grant, new OracleDialect(), 0).fragments().getFirst().value();
        assertEquals("GRANT SELECT, UPDATE ON CRM.CUSTOMER TO API_USER WITH GRANT OPTION", ddl);
    }
}
