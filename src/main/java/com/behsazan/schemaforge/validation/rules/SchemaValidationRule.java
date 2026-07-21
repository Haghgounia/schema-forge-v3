package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import com.behsazan.schemaforge.validation.domain.ValidationIssue;
import com.behsazan.schemaforge.validation.domain.ValidationSeverity;
import com.behsazan.schemaforge.validation.oracle.OracleIdentifierValidator;
import com.behsazan.schemaforge.validation.oracle.OracleLengthValidator;
import com.behsazan.schemaforge.validation.oracle.OracleReservedWordValidator;

import java.util.Objects;

/** Validates the root database schema definition. */
public final class SchemaValidationRule implements ValidationRule {

    public static final String ATTRIBUTE_SCHEMA = "schema";

    private static final String OBJECT_TYPE = "schema";

    private final OracleIdentifierValidator identifierValidator;
    private final OracleReservedWordValidator reservedWordValidator;
    private final OracleLengthValidator lengthValidator;

    public SchemaValidationRule() {
        this(
                new OracleIdentifierValidator(),
                new OracleReservedWordValidator(),
                new OracleLengthValidator()
        );
    }

    public SchemaValidationRule(
            OracleIdentifierValidator identifierValidator,
            OracleReservedWordValidator reservedWordValidator,
            OracleLengthValidator lengthValidator
    ) {
        this.identifierValidator = Objects.requireNonNull(
                identifierValidator,
                "identifierValidator must not be null"
        );
        this.reservedWordValidator = Objects.requireNonNull(
                reservedWordValidator,
                "reservedWordValidator must not be null"
        );
        this.lengthValidator = Objects.requireNonNull(
                lengthValidator,
                "lengthValidator must not be null"
        );
    }

    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");

        DatabaseSchema schema = context.get(ATTRIBUTE_SCHEMA);
        if (schema == null) {
            addIssue(
                    context,
                    ValidationCode.REQUIRED_VALUE,
                    "",
                    "Database schema is required."
            );
            return;
        }

        String schemaName = schema.name().value();

        validateIdentifier(context, schemaName);
        validateReservedWord(context, schemaName);
        validateLength(context, schemaName);
    }

    private void validateIdentifier(ValidationContext context, String schemaName) {
        try {
            identifierValidator.requireValid(schemaName, OBJECT_TYPE);
        } catch (IllegalArgumentException exception) {
            addIssue(
                    context,
                    schemaName == null || schemaName.isBlank()
                            ? ValidationCode.REQUIRED_VALUE
                            : ValidationCode.INVALID_IDENTIFIER,
                    schemaName,
                    exception.getMessage()
            );
        }
    }

    private void validateReservedWord(ValidationContext context, String schemaName) {
        try {
            reservedWordValidator.requireNotReserved(schemaName, OBJECT_TYPE);
        } catch (IllegalArgumentException exception) {
            addIssue(
                    context,
                    ValidationCode.RESERVED_WORD,
                    schemaName,
                    exception.getMessage()
            );
        }
    }

    private void validateLength(ValidationContext context, String schemaName) {
        try {
            lengthValidator.requireValidIdentifierLength(schemaName, OBJECT_TYPE);
        } catch (IllegalArgumentException exception) {
            addIssue(
                    context,
                    ValidationCode.IDENTIFIER_TOO_LONG,
                    schemaName,
                    exception.getMessage()
            );
        }
    }

    private void addIssue(
            ValidationContext context,
            ValidationCode code,
            String objectName,
            String message
    ) {
        context.result().addIssue(
                new ValidationIssue(
                        ValidationSeverity.ERROR,
                        code,
                        objectName,
                        message
                )
        );
    }
}
