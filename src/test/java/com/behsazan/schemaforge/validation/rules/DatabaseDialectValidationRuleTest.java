package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.dialect.standard.StandardDialect;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationDialectResolver;
import com.behsazan.schemaforge.validation.core.ValidationEngine;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseDialectValidationRuleTest {

    @Test
    void shouldUseDialectFromValidationContextForIdentifier() {
        ValidationContext context = new ValidationContext();
        ValidationDialectResolver.use(context, new StandardDialect());
        context.put(DatabaseIdentifierValidationRule.ATTRIBUTE_IDENTIFIER, "customer_account");
        context.put(DatabaseIdentifierValidationRule.ATTRIBUTE_OBJECT_TYPE, "table");

        var result = new ValidationEngine()
                .register(new DatabaseIdentifierValidationRule())
                .validate(context);

        assertTrue(result.isValid());
    }

    @Test
    void shouldUseDialectFromValidationContextForDataType() {
        ValidationContext context = new ValidationContext();
        ValidationDialectResolver.use(context, new StandardDialect());
        context.put(DatabaseDataTypeValidationRule.ATTRIBUTE_DATA_TYPE, "VARCHAR(200)");
        context.put(DatabaseDataTypeValidationRule.ATTRIBUTE_COLUMN_NAME, "CUSTOMER_NAME");

        var result = new ValidationEngine()
                .register(new DatabaseDataTypeValidationRule())
                .validate(context);

        assertTrue(result.isValid());
    }

    @Test
    void shouldRejectVendorSpecificTypeUnderStandardDialect() {
        ValidationContext context = new ValidationContext();
        ValidationDialectResolver.use(context, new StandardDialect());
        context.put(DatabaseDataTypeValidationRule.ATTRIBUTE_DATA_TYPE, "VARCHAR2(200)");
        context.put(DatabaseDataTypeValidationRule.ATTRIBUTE_COLUMN_NAME, "CUSTOMER_NAME");

        var result = new ValidationEngine()
                .register(new DatabaseDataTypeValidationRule())
                .validate(context);

        assertFalse(result.isValid());
        assertEquals(ValidationCode.UNSUPPORTED_DATA_TYPE, result.issues().getFirst().code());
    }
}
