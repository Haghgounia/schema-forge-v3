package com.behsazan.schemaforge.discovery.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.behsazan.schemaforge.discovery.core.DiscoveryContext;
import com.behsazan.schemaforge.discovery.domain.DiscoveryCategory;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.specification.domain.ColumnDefinition;
import com.behsazan.schemaforge.specification.domain.DataTypeDefinition;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class NullableConsistencyRuleTest {

    @Test
    void createsWarningWhenNullableFlagDiffersFromStandard() {
        DatabaseSchema schema = schema(Column.required("CUSTOMER_ID", DataType.numeric("NUMBER", 10, null)));
        TableDefinition document = document(true);

        var issues = new NullableConsistencyRule().evaluate(new DiscoveryContext(document, schema));

        assertThat(issues).hasSize(1);
        assertThat(issues.getFirst().category()).isEqualTo(DiscoveryCategory.NULLABLE_CONSISTENCY);
        assertThat(issues.getFirst().details())
                .containsEntry("standardNullable", "false")
                .containsEntry("documentNullable", "true");
    }

    @Test
    void producesNoWarningWhenNullableFlagMatches() {
        DatabaseSchema schema = schema(Column.nullable("CUSTOMER_ID", DataType.numeric("NUMBER", 10, null)));

        assertThat(new NullableConsistencyRule().evaluate(new DiscoveryContext(document(true), schema))).isEmpty();
    }

    private static DatabaseSchema schema(Column column) {
        return DatabaseSchema.builder("CIF")
                .addTable(Table.builder("CIF", "CUSTOMERS").addColumn(column).build())
                .build();
    }

    private static TableDefinition document(boolean nullable) {
        return new TableDefinition("CIF", "NEW_TABLE", null,
                List.of(new ColumnDefinition("CUSTOMER_ID", new DataTypeDefinition("NUMBER", null, 10, null),
                        nullable, null, null, false, false, false)),
                null, List.of(), List.of(), null, Map.of());
    }
}
