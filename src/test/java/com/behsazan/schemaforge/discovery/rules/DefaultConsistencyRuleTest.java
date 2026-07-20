package com.behsazan.schemaforge.discovery.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.behsazan.schemaforge.discovery.core.DiscoveryContext;
import com.behsazan.schemaforge.discovery.domain.DiscoveryCategory;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.DefaultValue;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.specification.domain.ColumnDefinition;
import com.behsazan.schemaforge.specification.domain.DataTypeDefinition;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DefaultConsistencyRuleTest {

    @Test
    void createsWarningWhenDefaultDiffersFromStandard() {
        DatabaseSchema schema = schema(column("1"));
        TableDefinition document = document("0");

        var issues = new DefaultConsistencyRule().evaluate(new DiscoveryContext(document, schema));

        assertThat(issues).hasSize(1);
        assertThat(issues.getFirst().category()).isEqualTo(DiscoveryCategory.DEFAULT_VALUE_CONSISTENCY);
        assertThat(issues.getFirst().details())
                .containsEntry("standardDefault", "1")
                .containsEntry("documentDefault", "0");
    }

    @Test
    void normalizesWhitespaceAndCaseBeforeComparison() {
        DatabaseSchema schema = schema(column("sysdate"));

        assertThat(new DefaultConsistencyRule().evaluate(new DiscoveryContext(document("  SYSDATE  "), schema))).isEmpty();
    }

    private static Column column(String defaultValue) {
        return new Column(Identifier.of("IS_ACTIVE"), DataType.numeric("NUMBER", 1, null), false,
                new DefaultValue(defaultValue), null, false, null);
    }

    private static DatabaseSchema schema(Column column) {
        return DatabaseSchema.builder("CIF")
                .addTable(Table.builder("CIF", "CUSTOMERS").addColumn(column).build())
                .build();
    }

    private static TableDefinition document(String defaultValue) {
        return new TableDefinition("CIF", "NEW_TABLE", null,
                List.of(new ColumnDefinition("IS_ACTIVE", new DataTypeDefinition("NUMBER", null, 1, null),
                        false, defaultValue, null, false, false, false)),
                null, List.of(), List.of(), null, Map.of());
    }
}
