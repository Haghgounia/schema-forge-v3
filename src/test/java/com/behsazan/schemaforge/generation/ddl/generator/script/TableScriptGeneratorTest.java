package com.behsazan.schemaforge.generation.ddl.generator.script;

import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.domain.model.CheckConstraint;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.PrimaryKey;
import com.behsazan.schemaforge.domain.model.Index;
import com.behsazan.schemaforge.domain.model.IndexColumn;
import com.behsazan.schemaforge.domain.enums.IndexType;
import com.behsazan.schemaforge.domain.enums.SortDirection;
import com.behsazan.schemaforge.domain.valueobject.Description;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.model.UniqueKey;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.generation.ddl.generator.table.TableDdlGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.table.oracle.OracleColumnDefinitionGenerator;
import com.behsazan.schemaforge.generation.ddl.model.RenderContext;
import com.behsazan.schemaforge.generation.ddl.model.ScriptOptions;
import com.behsazan.schemaforge.generation.ddl.renderer.oracle.OracleDdlRenderer;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TableScriptGeneratorTest {

    @Test
    void generatesOneCompleteScriptForTableAndLocalConstraints() {
        Table table = Table.builder("BANK", "CUSTOMER")
                .addColumn(Column.required("ID", DataType.numeric("NUMBER", 19, 0)))
                .addColumn(Column.required("EMAIL", DataType.varchar("VARCHAR2", 200)))
                .addColumn(Column.required("STATUS", DataType.varchar("VARCHAR2", 1)))
                .primaryKey(new PrimaryKey(Identifier.of("PK_CUSTOMER"), List.of(Identifier.of("ID"))))
                .addUniqueKey(new UniqueKey(Identifier.of("UK_CUSTOMER_EMAIL"), List.of(Identifier.of("EMAIL"))))
                .addCheck(new CheckConstraint(Identifier.of("CHK_CUSTOMER_STATUS"), "STATUS IN ('A','I')"))
                .addIndex(new Index(Identifier.of("IX_CUSTOMER_STATUS"),
                        List.of(new IndexColumn(Identifier.of("STATUS"), SortDirection.ASC)),
                        IndexType.NORMAL, Description.empty()))
                .build();

        OracleDialect dialect = new OracleDialect();
        TableScriptGenerator generator = new TableScriptGenerator(
                new TableDdlGenerator(new OracleColumnDefinitionGenerator()));
        var script = generator.generate(table, dialect);
        var rendered = new OracleDdlRenderer().render(
                script,
                new RenderContext(
                        dialect,
                        new ScriptOptions(false, false, true, true, "\n"),
                        Clock.systemUTC(),
                        Map.of()));

        assertEquals(5, rendered.statementCount());
        assertTrue(rendered.content().contains("CREATE TABLE BANK.CUSTOMER"));
        assertTrue(rendered.content().contains("ADD CONSTRAINT PK_CUSTOMER\nPRIMARY KEY (ID)"));
        assertTrue(rendered.content().contains("ADD CONSTRAINT UK_CUSTOMER_EMAIL\nUNIQUE (EMAIL)"));
        assertTrue(rendered.content().contains("ADD CONSTRAINT CHK_CUSTOMER_STATUS\nCHECK (STATUS IN ('A','I'))"));
        assertTrue(rendered.content().contains("CREATE INDEX BANK.IX_CUSTOMER_STATUS\nON BANK.CUSTOMER (STATUS ASC)"));
    }
}
