package com.behsazan.schemaforge.generation.ddl.generator.index;

import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.domain.enums.IndexType;
import com.behsazan.schemaforge.domain.enums.SortDirection;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.Index;
import com.behsazan.schemaforge.domain.model.IndexColumn;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.Description;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexGeneratorTest {

    @Test
    void generatesSchemaQualifiedOracleUniqueCompositeIndexWithSortDirections() {
        Table table = table(new Index(
                Identifier.of("UX_ORDERS_CUSTOMER_DATE"),
                List.of(
                        new IndexColumn(Identifier.of("CUSTOMER_ID"), SortDirection.ASC),
                        new IndexColumn(Identifier.of("ORDER_DATE"), SortDirection.DESC)),
                IndexType.UNIQUE,
                Description.empty()));

        var statements = new IndexGenerator().generate(table, new OracleDialect(), 0);

        assertEquals(1, statements.size());
        String ddl = statements.getFirst().fragments().getFirst().value();
        assertTrue(ddl.contains("CREATE UNIQUE INDEX SALES.UX_ORDERS_CUSTOMER_DATE"));
        assertTrue(ddl.contains("ON SALES.ORDERS (CUSTOMER_ID ASC, ORDER_DATE DESC)"));
    }

    @Test
    void rejectsUnsupportedOracleIndexType() {
        Table table = table(new Index(
                Identifier.of("IX_ORDERS_CLUSTERED"),
                List.of(new IndexColumn(Identifier.of("CUSTOMER_ID"), SortDirection.ASC)),
                IndexType.CLUSTERED,
                Description.empty()));

        assertThrows(IllegalArgumentException.class,
                () -> new IndexGenerator().generate(table, new OracleDialect(), 0));
    }

    private static Table table(Index index) {
        return Table.builder("SALES", "ORDERS")
                .addColumn(Column.required("ID", DataType.numeric("NUMBER", 19, 0)))
                .addColumn(Column.required("CUSTOMER_ID", DataType.numeric("NUMBER", 19, 0)))
                .addColumn(Column.required("ORDER_DATE", DataType.varchar("VARCHAR2", 30)))
                .addIndex(index)
                .build();
    }
}
