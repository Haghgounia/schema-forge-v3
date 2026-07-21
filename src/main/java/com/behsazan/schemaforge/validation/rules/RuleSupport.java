package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationDialectResolver;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import com.behsazan.schemaforge.validation.domain.ValidationIssue;
import com.behsazan.schemaforge.validation.domain.ValidationSeverity;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

final class RuleSupport {
    static final String ATTRIBUTE_SCHEMA = "schema";

    private RuleSupport() {
    }

    static void addError(ValidationContext context, ValidationCode code, String objectName, String message) {
        context.result().addIssue(new ValidationIssue(ValidationSeverity.ERROR, code, objectName, message));
    }

    static DatabaseDialect dialect(ValidationContext context) {
        return ValidationDialectResolver.resolve(context);
    }

    static void validateName(ValidationContext context, Identifier name, String objectType) {
        if (name == null) {
            return;
        }
        DatabaseDialect dialect = dialect(context);
        String value = name.value();
        String normalized = dialect.identifierRules().normalize(value);

        if (!dialect.identifierRules().isValidUnquotedIdentifier(normalized)) {
            ValidationCode code = normalized != null
                    && normalized.length() > dialect.identifierRules().maxIdentifierLength()
                    ? ValidationCode.IDENTIFIER_TOO_LONG
                    : ValidationCode.INVALID_IDENTIFIER;
            addError(context, code, value,
                    "Invalid " + dialect.name() + " " + objectType + " identifier: " + value);
        }
        if (dialect.identifierRules().isReservedWord(normalized)) {
            addError(context, ValidationCode.RESERVED_WORD, value,
                    objectType + " name is a reserved word in " + dialect.name() + ".");
        }
    }

    static boolean hasDuplicates(List<Identifier> names) {
        Set<String> seen = new HashSet<>();
        for (Identifier name : names) {
            if (!seen.add(name.normalized())) {
                return true;
            }
        }
        return false;
    }

    static String render(DataType type) {
        String name = type.name().value().toUpperCase(Locale.ROOT);
        if (type.length() != null) {
            return name + "(" + type.length() + ")";
        }
        if (type.precision() != null) {
            return type.scale() == null
                    ? name + "(" + type.precision() + ")"
                    : name + "(" + type.precision() + "," + type.scale() + ")";
        }
        return name;
    }
}
