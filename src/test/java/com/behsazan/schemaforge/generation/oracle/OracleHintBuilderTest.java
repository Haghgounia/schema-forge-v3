package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import com.behsazan.schemaforge.database.oracle.OracleDictionaryCache;
import com.behsazan.schemaforge.database.oracle.OracleMetadataProvider;
import com.behsazan.schemaforge.specification.domain.ColumnDefinition;
import com.behsazan.schemaforge.specification.domain.DataTypeDefinition;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OracleHintBuilderTest {

    @Test
    void generatesReservedWordUsageAndDataTypeConsistencyHints() {
        OracleMetadataProvider repository = mock(OracleMetadataProvider.class);
        when(repository.loadReservedWords()).thenReturn(Set.of("COMMENT"));
        when(repository.loadColumnUsageCounts()).thenReturn(Map.of("COMMENT", 5, "CUSTOMER_ID", 74));
        when(repository.loadColumnDataTypeUsages()).thenReturn(Map.of(
                "CUSTOMER_ID",
                List.of(
                        new ColumnDataTypeUsage("CUSTOMER_ID", "NUMBER", null, 10, 0, 70),
                        new ColumnDataTypeUsage("CUSTOMER_ID", "NUMBER", null, 20, 0, 4))));

        OracleDictionaryCache cache = new OracleDictionaryCache(repository);
        cache.initialize();

        TableDefinition table = new TableDefinition(
                "CIF",
                "CUSTOMERS",
                null,
                List.of(
                        new ColumnDefinition(
                                "COMMENT",
                                DataTypeDefinition.of("VARCHAR2"),
                                true,
                                null,
                                null,
                                false,
                                false,
                                false),
                        new ColumnDefinition(
                                "CUSTOMER_ID",
                                new DataTypeDefinition("NUMBER", null, 20, 0),
                                false,
                                null,
                                null,
                                true,
                                false,
                                false)),
                null,
                List.of(),
                List.of(),
                null,
                Map.of());

        String hints = new OracleHintBuilder(cache).build(table);

        assertThat(hints)
                .contains("Column COMMENT is an Oracle reserved word")
                .contains("Column COMMENT is used in 5 database table(s)")
                .contains("Column CUSTOMER_ID is used in 74 database table(s)")
                .contains("Column CUSTOMER_ID uses NUMBER(20) in the document")
                .contains("Existing standard type is NUMBER(10) across 70 database table(s)");
    }
}
