package com.behsazan.schemaforge.validation.oracle;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OracleLengthValidatorTest {

    private final OracleLengthValidator validator = new OracleLengthValidator();

    @Test
    void acceptsIdentifierAtOracleLimit() {
        assertThatCode(() -> validator.requireValidIdentifierLength("A".repeat(128), "table"))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsIdentifierBeyondOracleLimit() {
        assertThatThrownBy(() -> validator.requireValidIdentifierLength("A".repeat(129), "table"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("128 character limit");
    }
}
