package com.behsazan.schemaforge.database.oracle;

import com.behsazan.schemaforge.database.domain.ColumnState;
import com.behsazan.schemaforge.database.domain.ConstraintState;
import com.behsazan.schemaforge.domain.enums.ReferentialAction;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OracleCanonicalSchemaMapperPhase52Test {

    private final OracleCanonicalSchemaMapper mapper = new OracleCanonicalSchemaMapper();

    @Test
    void mapsOracleIdentityColumn() {
        var table = mapper.mapTable(
                "ACC", "IDENTITY_SAMPLE", null,
                List.of(new ColumnState(1, "ID", "NUMBER", 22, null, null, 18, 0,
                        false, null, null, true)),
                List.of(), List.of());

        assertThat(table.columns().getFirst().identity()).isTrue();
    }

    @Test
    void mapsOracleDeleteCascade() {
        var table = mapper.mapTable(
                "ACC", "CHILD_TABLE", null,
                List.of(new ColumnState(1, "PARENT_ID", "NUMBER", 22, null, null, 18, 0,
                        false, null, null, false)),
                List.of(new ConstraintState("FK_CHILD_PARENT", "R", "PARENT_ID", 1, null,
                        "ACC", "PARENT_TABLE", "ID", "CASCADE")),
                List.of());

        assertThat(table.foreignKeys()).hasSize(1);
        assertThat(table.foreignKeys().getFirst().onDelete()).isEqualTo(ReferentialAction.CASCADE);
    }
}
