package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;

/**
 * Backward-compatible Oracle adapter for the dialect-neutral rule.
 *
 * @deprecated use {@link DatabaseIdentifierValidationRule} with an active dialect.
 */
@Deprecated(forRemoval = false)
public final class OracleIdentifierValidationRule implements ValidationRule {

    public static final String ATTRIBUTE_IDENTIFIER = DatabaseIdentifierValidationRule.ATTRIBUTE_IDENTIFIER;
    public static final String ATTRIBUTE_OBJECT_TYPE = DatabaseIdentifierValidationRule.ATTRIBUTE_OBJECT_TYPE;
    public static final String ATTRIBUTE_SOURCE_NAME = DatabaseIdentifierValidationRule.ATTRIBUTE_SOURCE_NAME;

    private final DatabaseIdentifierValidationRule delegate =
            new DatabaseIdentifierValidationRule(new OracleDialect());

    @Override
    public void validate(ValidationContext context) {
        delegate.validate(context);
    }
}
