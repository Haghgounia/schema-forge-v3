package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.IdentifierRules;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationDialectResolver;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;

import java.util.Objects;

/** Validates an identifier using the active database dialect. */
public final class DatabaseIdentifierValidationRule implements ValidationRule {

    public static final String ATTRIBUTE_IDENTIFIER = "identifier";
    public static final String ATTRIBUTE_OBJECT_TYPE = "objectType";
    public static final String ATTRIBUTE_SOURCE_NAME = "sourceName";

    private final DatabaseDialect dialect;

    public DatabaseIdentifierValidationRule() {
        this.dialect = null;
    }

    public DatabaseIdentifierValidationRule(DatabaseDialect dialect) {
        this.dialect = Objects.requireNonNull(dialect, "dialect must not be null");
    }

    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");
        DatabaseDialect activeDialect = dialect == null
                ? ValidationDialectResolver.resolve(context)
                : dialect;

        String identifier = context.get(ATTRIBUTE_IDENTIFIER);
        String objectType = context.get(ATTRIBUTE_OBJECT_TYPE);
        if (objectType == null || objectType.isBlank()) {
            throw new IllegalStateException("Validation context attribute 'objectType' must not be blank");
        }

        String normalized = activeDialect.identifierRules().normalize(identifier);
        if (normalized == null || normalized.isBlank()) {
            RuleSupport.addError(context, ValidationCode.REQUIRED_VALUE, identifier,
                    "Missing " + objectType + " name.");
            return;
        }

        IdentifierRules rules = activeDialect.identifierRules();
        if (!rules.isValidUnquotedIdentifier(normalized)) {
            ValidationCode code = normalized.length() > rules.maxIdentifierLength()
                    ? ValidationCode.IDENTIFIER_TOO_LONG
                    : ValidationCode.INVALID_IDENTIFIER;
            RuleSupport.addError(context, code, normalized,
                    "Invalid " + activeDialect.name() + " " + objectType + " identifier '" + normalized + "'.");
        }
        if (rules.isReservedWord(normalized)) {
            RuleSupport.addError(context, ValidationCode.RESERVED_WORD, normalized,
                    objectType + " name is a reserved word in " + activeDialect.name() + ".");
        }
    }
}
