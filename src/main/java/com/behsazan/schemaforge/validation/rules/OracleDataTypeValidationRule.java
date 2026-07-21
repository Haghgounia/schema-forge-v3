package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;

/**
 * Backward-compatible Oracle adapter for the dialect-neutral rule.
 *
 * @deprecated use {@link DatabaseDataTypeValidationRule} with an active dialect.
 */
@Deprecated(forRemoval = false)
public final class OracleDataTypeValidationRule implements ValidationRule {

    public static final String ATTRIBUTE_DATA_TYPE = DatabaseDataTypeValidationRule.ATTRIBUTE_DATA_TYPE;
    public static final String ATTRIBUTE_COLUMN_NAME = DatabaseDataTypeValidationRule.ATTRIBUTE_COLUMN_NAME;

    private final DatabaseDataTypeValidationRule delegate =
            new DatabaseDataTypeValidationRule(new OracleDialect());

    @Override
    public void validate(ValidationContext context) {
        delegate.validate(context);
    }
}
