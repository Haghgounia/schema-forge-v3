package com.behsazan.schemaforge.validation.oracle;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OracleReservedWordValidatorTest {

    private final OracleReservedWordValidator validator = new OracleReservedWordValidator();

    @Test
    void recognizesReservedWordCaseInsensitively() {
        assertThat(validator.isReserved("select")).isTrue();
    }

    @Test
    void acceptsNonReservedIdentifier() {
        validator.requireNotReserved("CUSTOMER_ACCOUNT", "table");
    }

    @Test
    void rejectsReservedIdentifier() {
        assertThatThrownBy(() -> validator.requireNotReserved("TABLE", "table"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reserved word");
    }
}
