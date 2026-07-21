package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import com.behsazan.schemaforge.validation.domain.ValidationIssue;
import com.behsazan.schemaforge.validation.domain.ValidationSeverity;
import com.behsazan.schemaforge.validation.oracle.OracleLengthValidator;
import com.behsazan.schemaforge.validation.oracle.OracleReservedWordValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

final class RuleSupport {
    static final String ATTRIBUTE_SCHEMA = "schema";
    private static final int ORACLE_IDENTIFIER_LIMIT = 128;
    private static final OracleReservedWordValidator RESERVED = new OracleReservedWordValidator();
    private static final OracleLengthValidator LENGTH = new OracleLengthValidator();

    private RuleSupport() {
    }

    static void addError(ValidationContext context, ValidationCode code, String objectName, String message) {
        context.result().addIssue(new ValidationIssue(ValidationSeverity.ERROR, code, objectName, message));
    }

    static void validateOracleName(ValidationContext context, Identifier name, String objectType) {
        if (name == null) {
            return;
        }
        String value = name.value();
        try {
            RESERVED.requireNotReserved(value, objectType);
        } catch (IllegalArgumentException exception) {
            addError(context, ValidationCode.RESERVED_WORD, value,
                    objectType + " name is an Oracle reserved word.");
        }
        try {
            LENGTH.requireValidIdentifierLength(value, objectType);
        } catch (IllegalArgumentException exception) {
            addError(context, ValidationCode.IDENTIFIER_TOO_LONG, value, exception.getMessage());
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
