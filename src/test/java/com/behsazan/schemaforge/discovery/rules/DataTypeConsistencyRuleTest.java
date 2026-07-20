package com.behsazan.schemaforge.discovery.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.behsazan.schemaforge.discovery.core.DiscoveryContext;
import com.behsazan.schemaforge.discovery.domain.DiscoveryCategory;
import com.behsazan.schemaforge.discovery.domain.DiscoverySeverity;
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

class DataTypeConsistencyRuleTest {

    @Test
    void createsAdvisoryWarningWhenDocumentTypeDiffersFromExistingStandard() {
        DatabaseSchema schema = DatabaseSchema.builder("CIF")
                .addTable(table("CUSTOMERS", DataType.numeric("NUMBER", 10, null)))
                .addTable(table("ACCOUNTS", DataType.numeric("NUMBER", 10, null)))
                .addTable(table("HISTORY", DataType.numeric("NUMBER", 20, null)))
                .build();
        TableDefinition document = documentTable(new DataTypeDefinition("NUMBER", null, 20, null));

        var issues = new DataTypeConsistencyRule().evaluate(new DiscoveryContext(document, schema));

        assertThat(issues).hasSize(1);
        assertThat(issues.getFirst().severity()).isEqualTo(DiscoverySeverity.WARNING);
        assertThat(issues.getFirst().category()).isEqualTo(DiscoveryCategory.DATA_TYPE_CONSISTENCY);
        assertThat(issues.getFirst().details())
                .containsEntry("standardType", "NUMBER(10)")
                .containsEntry("documentType", "NUMBER(20)");
    }

    @Test
    void producesNoWarningWhenDocumentMatchesExistingStandard() {
        DatabaseSchema schema = DatabaseSchema.builder("CIF")
                .addTable(table("CUSTOMERS", DataType.numeric("NUMBER", 10, null)))
                .build();
        TableDefinition document = documentTable(new DataTypeDefinition("NUMBER", null, 10, null));

        var issues = new DataTypeConsistencyRule().evaluate(new DiscoveryContext(document, schema));

        assertThat(issues).isEmpty();
    }

    private static Table table(String name, DataType type) {
        return Table.builder("CIF", name)
                .addColumn(Column.required("CUSTOMER_ID", type))
                .build();
    }

    private static TableDefinition documentTable(DataTypeDefinition type) {
        return new TableDefinition("CIF", "NEW_TABLE", null,
                List.of(new ColumnDefinition("CUSTOMER_ID", type, false, null, null, false, false, false)),
                null, List.of(), List.of(), null, Map.of());
    }
}
