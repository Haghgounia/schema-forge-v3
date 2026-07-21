package com.behsazan.schemaforge.generation.oracle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import com.behsazan.schemaforge.database.oracle.OracleDictionaryCache;
import com.behsazan.schemaforge.database.oracle.OracleMetadataProvider;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import com.behsazan.schemaforge.generation.spi.GenerationContext;
import com.behsazan.schemaforge.generation.spi.GenerationOptions;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OracleMetadataAwareDdlGenerationTest {

    @Test
    void rendersUsageCountAndReportsReservedWordAndDatatypeMismatch() {
        OracleMetadataProvider repository = mock(OracleMetadataProvider.class);
        when(repository.loadReservedWords()).thenReturn(Set.of("COMMENT"));
        when(repository.loadColumnUsageCounts()).thenReturn(Map.of(
                "COMMENT", 5,
                "CUSTOMER_ID", 60));
        when(repository.loadColumnDataTypeUsages()).thenReturn(Map.of(
                "COMMENT", List.of(new ColumnDataTypeUsage(
                        "COMMENT", "VARCHAR2", 100, null, null, 5)),
                "CUSTOMER_ID", List.of(new ColumnDataTypeUsage(
                        "CUSTOMER_ID", "NUMBER", 22, 10, 0, 60))));

        OracleDictionaryCache cache = new OracleDictionaryCache(repository);
        cache.initialize();

        Table table = Table.builder("LON", "CONTRACTS")
                .addColumn(new Column(Identifier.of("COMMENT"), DataType.varchar("STRING", 100), true,
                        null, null, false, 1))
                .addColumn(new Column(Identifier.of("CUSTOMER_ID"), DataType.varchar("STRING", 20), false,
                        null, null, false, 2))
                .addColumn(new Column(Identifier.of("NEW_FIELD"), DataType.varchar("STRING", 30), true,
                        null, null, false, 3))
                .build();

        DatabaseSchema schema = DatabaseSchema.builder("LON").addTable(table).build();
        GenerationContext context = new GenerationContext(
                schema,
                DatabaseType.ORACLE,
                GenerationOptions.defaults(),
                Clock.systemUTC());

        String sql = new String(
                new OracleDdlGenerator(cache).generate(context).artifacts().getFirst().content(),
                StandardCharsets.UTF_8);

        assertThat(sql)
                .contains("-- ERROR: Column COMMENT is an Oracle reserved word.")
                .contains("/*   5*/  COMMENT VARCHAR2(100 CHAR)")
                .contains("-- WARNING: Column CUSTOMER_ID uses VARCHAR2(20CHAR) in the document; "
                        + "Oracle metadata standard is NUMBER(10) across 60 table(s).")
                .contains("/*  60*/  CUSTOMER_ID VARCHAR2(20 CHAR) NOT NULL")
                .contains("/*   0*/  NEW_FIELD VARCHAR2(30 CHAR)")
                .doesNotContain("WARNING: Column NEW_FIELD");
    }

    @Test
    void databaseFailureFallsBackToZeroAndDoesNotStopGeneration() {
        OracleMetadataProvider repository = mock(OracleMetadataProvider.class);
        when(repository.loadReservedWords()).thenThrow(new IllegalStateException("database unavailable"));
        when(repository.loadColumnUsageCounts()).thenThrow(new IllegalStateException("database unavailable"));
        when(repository.loadColumnDataTypeUsages()).thenThrow(new IllegalStateException("database unavailable"));

        OracleDictionaryCache cache = new OracleDictionaryCache(repository);
        cache.initialize();

        Table table = Table.builder("LON", "CONTRACTS")
                .addColumn(Column.required("CUSTOMER_ID", DataType.numeric("NUMBER", 10, null)))
                .build();
        DatabaseSchema schema = DatabaseSchema.builder("LON").addTable(table).build();

        String sql = new String(new OracleDdlGenerator(cache).generate(new GenerationContext(
                        schema, DatabaseType.ORACLE, GenerationOptions.defaults(), Clock.systemUTC()))
                .artifacts().getFirst().content(), StandardCharsets.UTF_8);

        assertThat(sql)
                .contains("/*   0*/  CUSTOMER_ID NUMBER(10) NOT NULL")
                .doesNotContain("ERROR: Column")
                .doesNotContain("WARNING: Column");
    }
}
