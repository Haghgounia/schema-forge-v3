package com.behsazan.schemaforge.domain.model;

import com.behsazan.schemaforge.domain.valueobject.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class CanonicalDomainModelTest {
    @Test void buildsValidTableAndSchema() {
        Table customer = Table.builder("BANK", "CUSTOMER")
                .addColumn(Column.required("CUSTOMER_ID", DataType.numeric("NUMBER", 18, 0)))
                .addColumn(Column.nullable("FULL_NAME", DataType.varchar("VARCHAR", 200)))
                .primaryKey(new PrimaryKey(Identifier.of("PK_CUSTOMER"), List.of(Identifier.of("CUSTOMER_ID"))))
                .build();
        DatabaseSchema schema = DatabaseSchema.builder("BANK").addTable(customer).build();
        assertThat(schema.findTable("customer")).contains(customer);
        assertThat(customer.findColumn("customer_id")).isPresent();
    }

    @Test void rejectsConstraintThatReferencesMissingColumn() {
        assertThatThrownBy(() -> Table.builder("BANK", "CUSTOMER")
                .addColumn(Column.required("ID", DataType.simple("BIGINT")))
                .primaryKey(new PrimaryKey(Identifier.of("PK_CUSTOMER"), List.of(Identifier.of("UNKNOWN"))))
                .build()).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("missing column");
    }

    @Test void rejectsDuplicateTableNamesIgnoringCase() {
        Table first = Table.builder("BANK", "CUSTOMER").addColumn(Column.required("ID", DataType.simple("BIGINT"))).build();
        Table second = Table.builder("bank", "customer").addColumn(Column.required("ID", DataType.simple("BIGINT"))).build();
        assertThatThrownBy(() -> DatabaseSchema.builder("BANK").addTable(first).addTable(second).build())
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("duplicate table");
    }
}
