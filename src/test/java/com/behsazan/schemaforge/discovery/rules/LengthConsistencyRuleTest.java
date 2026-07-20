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

class LengthConsistencyRuleTest {

    @Test
    void createsWarningWhenDocumentLengthDiffersFromStandard() {
        DatabaseSchema schema = schema(Column.required("IBAN", DataType.varchar("VARCHAR2", 26)));
        TableDefinition document = document(new ColumnDefinition("IBAN",
                new DataTypeDefinition("VARCHAR2", 40, null, null), false, null, null, false, false, false));

        var issues = new LengthConsistencyRule().evaluate(new DiscoveryContext(document, schema));

        assertThat(issues).hasSize(1);
        assertThat(issues.getFirst().category()).isEqualTo(DiscoveryCategory.LENGTH_CONSISTENCY);
        assertThat(issues.getFirst().details())
                .containsEntry("standardLength", "26")
                .containsEntry("documentLength", "40");
    }

    @Test
    void producesNoWarningWhenLengthMatches() {
        DatabaseSchema schema = schema(Column.required("IBAN", DataType.varchar("VARCHAR2", 26)));
        TableDefinition document = document(new ColumnDefinition("IBAN",
                new DataTypeDefinition("VARCHAR2", 26, null, null), false, null, null, false, false, false));

        assertThat(new LengthConsistencyRule().evaluate(new DiscoveryContext(document, schema))).isEmpty();
    }

    private static DatabaseSchema schema(Column column) {
        return DatabaseSchema.builder("CIF")
                .addTable(Table.builder("CIF", "CUSTOMERS").addColumn(column).build())
                .build();
    }

    private static TableDefinition document(ColumnDefinition column) {
        return new TableDefinition("CIF", "NEW_TABLE", null, List.of(column), null, List.of(), List.of(), null, Map.of());
    }
}
