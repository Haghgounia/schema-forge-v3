package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationEngine;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import com.behsazan.schemaforge.validation.domain.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OracleIdentifierValidationRuleTest {

    @Test
    void shouldAcceptValidIdentifier() {

        ValidationContext context = new ValidationContext();

        context.put(
                OracleIdentifierValidationRule.ATTRIBUTE_IDENTIFIER,
                "CUSTOMER_ACCOUNT"
        );

        context.put(
                OracleIdentifierValidationRule.ATTRIBUTE_OBJECT_TYPE,
                "table"
        );

        context.put(
                OracleIdentifierValidationRule.ATTRIBUTE_SOURCE_NAME,
                "customer.docx"
        );

        ValidationResult result = new ValidationEngine()
                .register(new OracleIdentifierValidationRule())
                .validate(context);

        assertTrue(result.isValid());
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRejectInvalidIdentifier() {

        ValidationContext context = new ValidationContext();

        context.put(
                OracleIdentifierValidationRule.ATTRIBUTE_IDENTIFIER,
                "123 TABLE"
        );

        context.put(
                OracleIdentifierValidationRule.ATTRIBUTE_OBJECT_TYPE,
                "table"
        );

        context.put(
                OracleIdentifierValidationRule.ATTRIBUTE_SOURCE_NAME,
                "customer.docx"
        );

        ValidationResult result = new ValidationEngine()
                .register(new OracleIdentifierValidationRule())
                .validate(context);

        assertFalse(result.isValid());
        assertEquals(1, result.size());

        assertEquals(
                ValidationCode.INVALID_IDENTIFIER,
                result.issues().getFirst().code()
        );
    }

    @Test
    void shouldRejectBlankIdentifier() {

        ValidationContext context = new ValidationContext();

        context.put(
                OracleIdentifierValidationRule.ATTRIBUTE_IDENTIFIER,
                ""
        );

        context.put(
                OracleIdentifierValidationRule.ATTRIBUTE_OBJECT_TYPE,
                "table"
        );

        context.put(
                OracleIdentifierValidationRule.ATTRIBUTE_SOURCE_NAME,
                "customer.docx"
        );

        ValidationResult result = new ValidationEngine()
                .register(new OracleIdentifierValidationRule())
                .validate(context);

        assertFalse(result.isValid());

        assertEquals(
                ValidationCode.REQUIRED_VALUE,
                result.issues().getFirst().code()
        );
    }
}