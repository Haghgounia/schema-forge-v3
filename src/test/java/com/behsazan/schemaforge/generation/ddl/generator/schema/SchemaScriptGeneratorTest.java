package com.behsazan.schemaforge.generation.ddl.generator.schema;

import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.domain.model.*;
import com.behsazan.schemaforge.domain.valueobject.*;
import com.behsazan.schemaforge.generation.ddl.generator.script.TableScriptGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.table.TableDdlGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.table.oracle.OracleColumnDefinitionGenerator;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SchemaScriptGeneratorTest {
    @Test void ordersSequenceBeforeTableAndGrantAfterComments() {
        Table table = Table.builder("CRM", "CUSTOMER").description("Customers")
                .addColumn(Column.required("ID", DataType.numeric("NUMBER", 19, 0))).build();
        DatabaseSchema schema = DatabaseSchema.builder("CRM")
                .addSequence(new Sequence(QualifiedName.of("CRM", "SEQ_CUSTOMER"), 1, 1, null, null, false, 20, Description.empty()))
                .addTable(table)
                .addGrant(new Grant(QualifiedName.of("CRM", "CUSTOMER"), "TABLE", Identifier.of("APP_USER"), List.of("SELECT"), false))
                .build();
        var generator = new SchemaScriptGenerator(new TableScriptGenerator(new TableDdlGenerator(new OracleColumnDefinitionGenerator())));
        var script = generator.generate(schema, new OracleDialect());
        assertEquals("Sequences", script.sections().getFirst().name());
        assertEquals("Grants", script.sections().getLast().name());
    }
}
