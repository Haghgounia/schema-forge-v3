package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import com.behsazan.schemaforge.validation.domain.ValidationIssue;
import com.behsazan.schemaforge.validation.domain.ValidationSeverity;
import com.behsazan.schemaforge.validation.oracle.OracleIdentifierValidator;

import java.util.Objects;

/**
 * Validates an Oracle identifier stored in the validation context.
 */
public final class OracleIdentifierValidationRule implements ValidationRule {

    public static final String ATTRIBUTE_IDENTIFIER = "identifier";
    public static final String ATTRIBUTE_OBJECT_TYPE = "objectType";
    public static final String ATTRIBUTE_SOURCE_NAME = "sourceName";

    private final OracleIdentifierValidator identifierValidator;

    public OracleIdentifierValidationRule() {
        this(new OracleIdentifierValidator());
    }

    public OracleIdentifierValidationRule(
            OracleIdentifierValidator identifierValidator
    ) {
        this.identifierValidator = Objects.requireNonNull(
                identifierValidator,
                "identifierValidator must not be null"
        );
    }

    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");

        String identifier = context.get(ATTRIBUTE_IDENTIFIER);
        String objectType = context.get(ATTRIBUTE_OBJECT_TYPE);
        String sourceName = context.get(ATTRIBUTE_SOURCE_NAME);

        if (objectType == null || objectType.isBlank()) {
            throw new IllegalStateException(
                    "Validation context attribute 'objectType' must not be blank"
            );
        }

        try {
            identifierValidator.requireValid(
                    identifier,
                    objectType,
                    sourceName
            );
        } catch (IllegalArgumentException exception) {
            context.result().addIssue(
                    new ValidationIssue(
                            ValidationSeverity.ERROR,
                            resolveCode(identifier),
                            identifier,
                            exception.getMessage()
                    )
            );
        }
    }

    private ValidationCode resolveCode(String identifier) {
        return identifier == null || identifier.isBlank()
                ? ValidationCode.REQUIRED_VALUE
                : ValidationCode.INVALID_IDENTIFIER;
    }
}