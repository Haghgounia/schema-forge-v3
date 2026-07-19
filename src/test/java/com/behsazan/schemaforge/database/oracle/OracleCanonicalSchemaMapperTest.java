package com.behsazan.schemaforge.database.oracle;

import static org.assertj.core.api.Assertions.assertThat;

import com.behsazan.schemaforge.database.domain.ColumnState;
import com.behsazan.schemaforge.database.domain.ConstraintState;
import com.behsazan.schemaforge.database.domain.IndexState;
import com.behsazan.schemaforge.domain.enums.IndexType;
import com.behsazan.schemaforge.domain.model.Table;
import java.util.List;
import org.junit.jupiter.api.Test;

class OracleCanonicalSchemaMapperTest {
    private final OracleCanonicalSchemaMapper mapper = new OracleCanonicalSchemaMapper();

    @Test
    void mapsOracleTableMetadataToCanonicalModel() {
        Table table = mapper.mapTable(
                "BIM", "PROVINCES", "Province reference table",
                List.of(
                        new ColumnState(1, "ID", "NUMBER", 22, null, null, 18, 0, false, null, "Identifier"),
                        new ColumnState(2, "TITLE", "VARCHAR2", 100, 100, "C", null, null, false, null, "Title")),
                List.of(
                        new ConstraintState("PK_PROVINCES", "P", "ID", 1, null, null, null, null),
                        new ConstraintState("UK_PROVINCES_TITLE", "U", "TITLE", 1, null, null, null, null)),
                List.of(new IndexState("IX_PROVINCES_TITLE", false, "TITLE", 1, "ASC")));

        assertThat(table.qualifiedName().toString()).isEqualTo("BIM.PROVINCES");
        assertThat(table.columns()).hasSize(2);
        assertThat(table.columns().getFirst().dataType().name().value()).isEqualTo("NUMBER");
        assertThat(table.primaryKey()).isPresent();
        assertThat(table.uniqueKeys()).hasSize(1);
        assertThat(table.indexes()).singleElement().satisfies(index -> assertThat(index.type()).isEqualTo(IndexType.NORMAL));
    }
}
