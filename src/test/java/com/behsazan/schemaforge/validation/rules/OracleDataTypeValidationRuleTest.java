package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationEngine;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import com.behsazan.schemaforge.validation.domain.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OracleDataTypeValidationRuleTest {

    @Test
    void shouldAcceptNumber() {

        ValidationContext context = new ValidationContext();

        context.put(
                OracleDataTypeValidationRule.ATTRIBUTE_DATA_TYPE,
                "NUMBER(18,2)"
        );

        context.put(
                OracleDataTypeValidationRule.ATTRIBUTE_COLUMN_NAME,
                "AMOUNT"
        );

        ValidationResult result = new ValidationEngine()
                .register(new OracleDataTypeValidationRule())
                .validate(context);

        assertTrue(result.isValid());
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldAcceptVarchar2Char() {

        ValidationContext context = new ValidationContext();

        context.put(
                OracleDataTypeValidationRule.ATTRIBUTE_DATA_TYPE,
                "VARCHAR2(200 CHAR)"
        );

        context.put(
                OracleDataTypeValidationRule.ATTRIBUTE_COLUMN_NAME,
                "CUSTOMER_NAME"
        );

        ValidationResult result = new ValidationEngine()
                .register(new OracleDataTypeValidationRule())
                .validate(context);

        assertTrue(result.isValid());
    }

    @Test
    void shouldRejectInvalidType() {

        ValidationContext context = new ValidationContext();

        context.put(
                OracleDataTypeValidationRule.ATTRIBUTE_DATA_TYPE,
                "NUMBER2(10)"
        );

        context.put(
                OracleDataTypeValidationRule.ATTRIBUTE_COLUMN_NAME,
                "AMOUNT"
        );

        ValidationResult result = new ValidationEngine()
                .register(new OracleDataTypeValidationRule())
                .validate(context);

        assertFalse(result.isValid());
        assertEquals(1, result.size());

        assertEquals(
                ValidationCode.UNSUPPORTED_DATA_TYPE,
                result.issues().getFirst().code()
        );
    }

    @Test
    void shouldRejectBlankType() {

        ValidationContext context = new ValidationContext();

        context.put(
                OracleDataTypeValidationRule.ATTRIBUTE_DATA_TYPE,
                ""
        );

        context.put(
                OracleDataTypeValidationRule.ATTRIBUTE_COLUMN_NAME,
                "AMOUNT"
        );

        ValidationResult result = new ValidationEngine()
                .register(new OracleDataTypeValidationRule())
                .validate(context);

        assertFalse(result.isValid());

        assertEquals(
                ValidationCode.REQUIRED_VALUE,
                result.issues().getFirst().code()
        );
    }
}