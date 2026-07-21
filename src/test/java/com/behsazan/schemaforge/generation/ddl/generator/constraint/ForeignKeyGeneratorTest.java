package com.behsazan.schemaforge.generation.ddl.generator.constraint;

import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.domain.enums.ReferentialAction;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.ForeignKey;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.domain.valueobject.QualifiedName;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForeignKeyGeneratorTest {
    @Test
    void generatesOracleCompositeForeignKeyWithDeleteCascade() {
        Table table = table(new ForeignKey(
                Identifier.of("FK_ORDER_CUSTOMER"),
                List.of(Identifier.of("CUSTOMER_ID"), Identifier.of("CUSTOMER_BRANCH")),
                QualifiedName.of("CRM", "CUSTOMER"),
                List.of(Identifier.of("ID"), Identifier.of("BRANCH_ID")),
                ReferentialAction.CASCADE,
                ReferentialAction.NO_ACTION));

        var statements = new ForeignKeyGenerator().generate(table, new OracleDialect(), 0);

        assertEquals(1, statements.size());
        String ddl = statements.getFirst().fragments().getFirst().value();
        assertTrue(ddl.contains("ADD CONSTRAINT FK_ORDER_CUSTOMER"));
        assertTrue(ddl.contains("FOREIGN KEY (CUSTOMER_ID, CUSTOMER_BRANCH)"));
        assertTrue(ddl.contains("REFERENCES CRM.CUSTOMER (ID, BRANCH_ID)"));
        assertTrue(ddl.contains("ON DELETE CASCADE"));
    }

    @Test
    void rejectsOracleOnUpdateAction() {
        Table table = table(new ForeignKey(null,
                List.of(Identifier.of("CUSTOMER_ID")), QualifiedName.of("CRM", "CUSTOMER"),
                List.of(Identifier.of("ID")), ReferentialAction.NO_ACTION, ReferentialAction.CASCADE));

        assertThrows(IllegalArgumentException.class,
                () -> new ForeignKeyGenerator().generate(table, new OracleDialect(), 0));
    }

    private static Table table(ForeignKey foreignKey) {
        return Table.builder("SALES", "ORDERS")
                .addColumn(Column.required("ID", DataType.numeric("NUMBER", 19, 0)))
                .addColumn(Column.required("CUSTOMER_ID", DataType.numeric("NUMBER", 19, 0)))
                .addColumn(Column.required("CUSTOMER_BRANCH", DataType.numeric("NUMBER", 10, 0)))
                .addForeignKey(foreignKey)
                .build();
    }
}
