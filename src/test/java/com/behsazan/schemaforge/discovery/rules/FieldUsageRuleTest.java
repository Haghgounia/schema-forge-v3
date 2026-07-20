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

class FieldUsageRuleTest {

    @Test
    void reportsEveryExistingTableThatUsesTheColumn() {
        DatabaseSchema schema = DatabaseSchema.builder("CIF")
                .addTable(table("CUSTOMERS", "CUSTOMER_ID"))
                .addTable(table("ACCOUNTS", "CUSTOMER_ID"))
                .build();
        TableDefinition document = documentTable("CUSTOMER_ID", DataTypeDefinition.of("NUMBER"));

        var issues = new FieldUsageRule().evaluate(new DiscoveryContext(document, schema));

        assertThat(issues).hasSize(1);
        assertThat(issues.getFirst().category()).isEqualTo(DiscoveryCategory.FIELD_USAGE);
        assertThat(issues.getFirst().details().get("usageCount")).isEqualTo("2");
        assertThat(issues.getFirst().details().get("locations")).contains("CIF.CUSTOMERS", "CIF.ACCOUNTS");
    }

    private static Table table(String name, String columnName) {
        return Table.builder("CIF", name)
                .addColumn(Column.required(columnName, DataType.numeric("NUMBER", 10, null)))
                .build();
    }

    private static TableDefinition documentTable(String columnName, DataTypeDefinition type) {
        return new TableDefinition("CIF", "NEW_TABLE", null,
                List.of(new ColumnDefinition(columnName, type, false, null, null, false, false, false)),
                null, List.of(), List.of(), null, Map.of());
    }
}
