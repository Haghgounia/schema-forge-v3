package com.behsazan.schemaforge.database.oracle;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OracleDictionaryCacheTest {

    @Test
    void normalizesDictionaryLookups() {
        OracleMetadataProvider repository = mock(OracleMetadataProvider.class);
        when(repository.loadReservedWords()).thenReturn(Set.of("SELECT"));
        when(repository.loadColumnUsageCounts()).thenReturn(Map.of("CUSTOMER_ID", 12));
        when(repository.loadColumnDataTypeUsages()).thenReturn(Map.of(
                "CUSTOMER_ID",
                List.of(new ColumnDataTypeUsage("CUSTOMER_ID", "NUMBER", null, 10, 0, 12))));

        OracleDictionaryCache cache = new OracleDictionaryCache(repository);
        cache.initialize();

        assertThat(cache.isReservedWord(" select ")).isTrue();
        assertThat(cache.getColumnUsageCount("customer_id")).isEqualTo(12);
        assertThat(cache.getColumnDataTypeUsages(" customer_id ")).hasSize(1);
    }
}
