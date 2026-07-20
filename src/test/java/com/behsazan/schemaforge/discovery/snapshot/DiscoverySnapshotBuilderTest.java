package com.behsazan.schemaforge.discovery.snapshot;

import static org.assertj.core.api.Assertions.assertThat;

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

class DiscoverySnapshotBuilderTest {

    @Test
    void indexesColumnUsageCaseInsensitively() {
        DatabaseSchema schema = DatabaseSchema.builder("CIF")
                .addTable(Table.builder("CIF", "CUSTOMERS")
                        .addColumn(Column.required("CUSTOMER_ID", DataType.numeric("NUMBER", 10, null)))
                        .build())
                .addTable(Table.builder("CIF", "ACCOUNTS")
                        .addColumn(Column.required("customer_id", DataType.numeric("NUMBER", 10, null)))
                        .build())
                .build();
        TableDefinition document = new TableDefinition("CIF", "NEW_TABLE", null,
                List.of(new ColumnDefinition("Customer_Id", new DataTypeDefinition("NUMBER", null, 10, null),
                        false, null, null, false, false, false)),
                null, List.of(), List.of(), null, Map.of());

        DiscoverySnapshot snapshot = new DiscoverySnapshotBuilder().build(document, schema);

        assertThat(snapshot.findColumnUsage("CUSTOMER_ID")).hasSize(2);
        assertThat(snapshot.findColumnUsage(" customer_id ")).hasSize(2);
        assertThat(snapshot.findColumnUsage(null)).isEmpty();
    }
}
