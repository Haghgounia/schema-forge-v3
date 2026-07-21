package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationEngine;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import com.behsazan.schemaforge.validation.domain.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SchemaValidationRuleTest {

    @Test
    void shouldAcceptValidSchema() {
        ValidationResult result = validate(DatabaseSchema.builder("BANKING").build());

        assertTrue(result.isValid());
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRejectMissingSchema() {
        ValidationResult result = validate(null);

        assertFalse(result.isValid());
        assertEquals(1, result.size());
        assertEquals(ValidationCode.REQUIRED_VALUE, result.issues().getFirst().code());
    }

    @Test
    void shouldRejectReservedSchemaName() {
        ValidationResult result = validate(DatabaseSchema.builder("USER").build());

        assertFalse(result.isValid());
        assertEquals(1, result.size());
        assertEquals(ValidationCode.RESERVED_WORD, result.issues().getFirst().code());
    }

    @Test
    void shouldRejectSchemaNameBeyondOracleLimit() {
        ValidationResult result = validate(
                DatabaseSchema.builder("S".repeat(129)).build()
        );

        assertFalse(result.isValid());
        assertEquals(2, result.size());
        assertTrue(result.issues().stream()
                .anyMatch(issue -> issue.code() == ValidationCode.INVALID_IDENTIFIER));
        assertTrue(result.issues().stream()
                .anyMatch(issue -> issue.code() == ValidationCode.IDENTIFIER_TOO_LONG));
    }

    private ValidationResult validate(DatabaseSchema schema) {
        ValidationContext context = new ValidationContext();
        if (schema != null) {
            context.put(SchemaValidationRule.ATTRIBUTE_SCHEMA, schema);
        }

        return new ValidationEngine()
                .register(new SchemaValidationRule())
                .validate(context);
    }
}
