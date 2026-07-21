package com.behsazan.schemaforge.generation.enrichment;

import static org.assertj.core.api.Assertions.assertThat;

import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import org.junit.jupiter.api.Test;

class AuditColumnSchemaEnricherTest {
    private final AuditColumnSchemaEnricher enricher = new AuditColumnSchemaEnricher();

    @Test
    void appendsMissingAuditColumnsInStandardOrder() {
        Table table = Table.builder("APP", "CUSTOMER")
                .addColumn(Column.required("ID", DataType.numeric("NUMBER", 18, 0)))
                .build();
        DatabaseSchema schema = DatabaseSchema.builder("APP").addTable(table).build();

        Table enriched = enricher.enrich(schema).tables().getFirst();

        assertThat(enriched.columns()).extracting(column -> column.name().value())
                .containsExactly("ID", "CREATED_BY", "CREATED_DATE", "LAST_MODIFIED_BY", "LAST_MODIFIED_DATE");
        assertThat(enriched.findColumn("CREATED_BY").orElseThrow().nullable()).isFalse();
        assertThat(enriched.findColumn("CREATED_DATE").orElseThrow().nullable()).isFalse();
        assertThat(enriched.findColumn("LAST_MODIFIED_BY").orElseThrow().nullable()).isTrue();
        assertThat(enriched.findColumn("LAST_MODIFIED_DATE").orElseThrow().nullable()).isTrue();
    }

    @Test
    void doesNotDuplicateAuditColumnsAlreadyDeclaredByDocument() {
        Table table = Table.builder("APP", "CUSTOMER")
                .addColumn(Column.required("ID", DataType.numeric("NUMBER", 18, 0)))
                .addColumn(Column.nullable("created_by", DataType.varchar("VARCHAR", 100)))
                .build();
        DatabaseSchema schema = DatabaseSchema.builder("APP").addTable(table).build();

        Table enriched = enricher.enrich(schema).tables().getFirst();

        assertThat(enriched.columns()).filteredOn(column -> column.name().normalized().equals("CREATED_BY"))
                .hasSize(1)
                .first().extracting(column -> column.dataType().length()).isEqualTo(100);
    }
}
