package com.behsazan.schemaforge.database.oracle;

import static org.assertj.core.api.Assertions.assertThat;

import com.behsazan.schemaforge.database.domain.RoutineState;
import com.behsazan.schemaforge.database.domain.SequenceState;
import com.behsazan.schemaforge.database.domain.SynonymState;
import com.behsazan.schemaforge.database.domain.TriggerState;
import com.behsazan.schemaforge.database.domain.ViewState;
import com.behsazan.schemaforge.domain.enums.RoutineType;
import org.junit.jupiter.api.Test;

class OracleExtendedCanonicalSchemaMapperTest {
    private final OracleCanonicalSchemaMapper mapper = new OracleCanonicalSchemaMapper();

    @Test
    void mapsExtendedOracleObjects() {
        var sequence = mapper.mapSequence("BIM", new SequenceState("SEQ_PROVINCES", 1, 999999, 1, false, 20, 100));
        var view = mapper.mapView("BIM", new ViewState("V_PROVINCES", "select * from provinces", false));
        var synonym = mapper.mapSynonym("BIM", new SynonymState("PROVINCES", "BIM", "PROVINCES", true));
        var trigger = mapper.mapTrigger(new TriggerState("TRG_PROVINCES", "BIM", "PROVINCES", "BEFORE EACH ROW", "INSERT", "BEGIN NULL; END;"));
        var routine = mapper.mapRoutine("BIM", new RoutineState("REFRESH_PROVINCES", "PROCEDURE", "BEGIN NULL; END;"));

        assertThat(sequence.qualifiedName().toString()).isEqualTo("BIM.SEQ_PROVINCES");
        assertThat(view.materialized()).isFalse();
        assertThat(synonym.publicSynonym()).isTrue();
        assertThat(trigger.table().toString()).isEqualTo("BIM.PROVINCES");
        assertThat(routine.routineType()).isEqualTo(RoutineType.PROCEDURE);
    }
}
