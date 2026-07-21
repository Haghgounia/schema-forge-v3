package com.behsazan.schemaforge.database.postgresql;

import static org.assertj.core.api.Assertions.assertThat;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import org.junit.jupiter.api.Test;

class PostgreSqlDictionaryModelTest {

    @Test
    void rendersPostgreSqlCharacterAndNumericSignatures() {
        assertThat(new ColumnDataTypeUsage("CODE", "CHARACTER VARYING", 40, null, null, 3).typeSignature())
                .isEqualTo("CHARACTER VARYING(40)");
        assertThat(new ColumnDataTypeUsage("AMOUNT", "NUMERIC", null, 18, 2, 7).typeSignature())
                .isEqualTo("NUMERIC(18,2)");
    }
}
