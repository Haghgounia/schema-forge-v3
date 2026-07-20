package com.behsazan.schemaforge.discovery.core;

import static org.assertj.core.api.Assertions.assertThat;

import com.behsazan.schemaforge.database.spi.DatabaseMetadataProvider;
import com.behsazan.schemaforge.discovery.domain.DiscoveryCategory;
import com.behsazan.schemaforge.discovery.rules.DataTypeConsistencyRule;
import com.behsazan.schemaforge.discovery.rules.FieldUsageRule;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import com.behsazan.schemaforge.specification.domain.ColumnDefinition;
import com.behsazan.schemaforge.specification.domain.DataTypeDefinition;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DiscoveryEngineTest {

    @Test
    void inspectsSchemaOnceAndExecutesAllRules() {
        DatabaseSchema schema = DatabaseSchema.builder("CIF")
                .addTable(Table.builder("CIF", "CUSTOMERS")
                        .addColumn(Column.required("CUSTOMER_ID", DataType.numeric("NUMBER", 10, null)))
                        .build())
                .build();
        DatabaseMetadataProvider provider = new StubProvider(schema);
        DiscoveryEngine engine = new DiscoveryEngine(provider,
                List.of(new FieldUsageRule(), new DataTypeConsistencyRule()));
        TableDefinition document = new TableDefinition("CIF", "CONTRACTS", null,
                List.of(new ColumnDefinition("CUSTOMER_ID",
                        new DataTypeDefinition("NUMBER", null, 20, null),
                        false, null, null, false, false, false)),
                null, List.of(), List.of(), null, Map.of());

        var result = engine.discover(document);

        assertThat(result.issues()).extracting(issue -> issue.category())
                .containsExactly(DiscoveryCategory.FIELD_USAGE, DiscoveryCategory.DATA_TYPE_CONSISTENCY);
    }

    private record StubProvider(DatabaseSchema schema) implements DatabaseMetadataProvider {
        @Override
        public DatabaseType databaseType() {
            return DatabaseType.ORACLE;
        }

        @Override
        public DatabaseSchema inspectSchema(String schemaName) {
            return schema;
        }
    }
}
