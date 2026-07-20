package com.behsazan.schemaforge.validation.oracle;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OracleIdentifierValidatorTest {
    private final OracleIdentifierValidator validator = new OracleIdentifierValidator();

    @Test
    void normalizesValidIdentifier() {
        assertThat(validator.requireValid(" customer_id ", "column")).isEqualTo("CUSTOMER_ID");
    }

    @Test
    void rejectsIdentifierContainingHyphen() {
        assertThatThrownBy(() -> validator.requireValid("CUSTOMER-ID", "column", "sample.docx"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid Oracle column identifier")
                .hasMessageContaining("sample.docx");
    }

    @Test
    void rejectsIdentifierStartingWithDigit() {
        assertThatThrownBy(() -> validator.requireValid("1CUSTOMER", "table"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must start with a letter");
    }

    @Test
    void rejectsIdentifierLongerThanOracleLimit() {
        assertThatThrownBy(() -> validator.requireValid("A".repeat(129), "constraint"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("128 character limit");
    }
}
