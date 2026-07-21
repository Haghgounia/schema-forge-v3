package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import com.behsazan.schemaforge.validation.domain.ValidationIssue;
import com.behsazan.schemaforge.validation.domain.ValidationSeverity;
import com.behsazan.schemaforge.validation.oracle.OracleLengthValidator;
import com.behsazan.schemaforge.validation.oracle.OracleReservedWordValidator;

import java.util.Objects;

/** Validates Oracle-specific table naming rules not enforced by the domain model. */
public final class TableValidationRule implements ValidationRule {

    private static final String OBJECT_TYPE = "table";

    private final OracleReservedWordValidator reservedWordValidator;
    private final OracleLengthValidator lengthValidator;

    public TableValidationRule() {
        this(new OracleReservedWordValidator(), new OracleLengthValidator());
    }

    public TableValidationRule(
            OracleReservedWordValidator reservedWordValidator,
            OracleLengthValidator lengthValidator
    ) {
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

        DatabaseSchema schema = context.get(SchemaValidationRule.ATTRIBUTE_SCHEMA);
        if (schema == null) {
            return;
        }

        for (Table table : schema.tables()) {
            validateTable(context, table);
        }
    }

    private void validateTable(ValidationContext context, Table table) {
        String tableName = table.qualifiedName().name().value();

        validateReservedWord(context, tableName);
        validateLength(context, tableName);
    }

    private void validateReservedWord(ValidationContext context, String tableName) {
        try {
            reservedWordValidator.requireNotReserved(tableName, OBJECT_TYPE);
        } catch (IllegalArgumentException exception) {
            addIssue(
                    context,
                    ValidationCode.RESERVED_WORD,
                    tableName,
                    exception.getMessage()
            );
        }
    }

    private void validateLength(ValidationContext context, String tableName) {
        try {
            lengthValidator.requireValidIdentifierLength(tableName, OBJECT_TYPE);
        } catch (IllegalArgumentException exception) {
            addIssue(
                    context,
                    ValidationCode.IDENTIFIER_TOO_LONG,
                    tableName,
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
