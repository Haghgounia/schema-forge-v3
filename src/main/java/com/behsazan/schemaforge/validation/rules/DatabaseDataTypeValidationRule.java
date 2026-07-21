package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationDialectResolver;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;

import java.util.Objects;

/** Validates a data type using the active database dialect. */
public final class DatabaseDataTypeValidationRule implements ValidationRule {

    public static final String ATTRIBUTE_DATA_TYPE = "dataType";
    public static final String ATTRIBUTE_COLUMN_NAME = "columnName";

    private final DatabaseDialect dialect;

    public DatabaseDataTypeValidationRule() {
        this.dialect = null;
    }

    public DatabaseDataTypeValidationRule(DatabaseDialect dialect) {
        this.dialect = Objects.requireNonNull(dialect, "dialect must not be null");
    }

    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");
        DatabaseDialect activeDialect = dialect == null
                ? ValidationDialectResolver.resolve(context)
                : dialect;
        String dataType = context.get(ATTRIBUTE_DATA_TYPE);
        String columnName = context.get(ATTRIBUTE_COLUMN_NAME);

        if (dataType == null || dataType.isBlank()) {
            RuleSupport.addError(context, ValidationCode.REQUIRED_VALUE, columnName, "Data type is required.");
            return;
        }

        if (!activeDialect.dataTypeRules().supports(dataType)) {
            RuleSupport.addError(context, ValidationCode.UNSUPPORTED_DATA_TYPE, columnName,
                    "Unsupported " + activeDialect.name() + " data type: " + dataType);
        }
    }
}
