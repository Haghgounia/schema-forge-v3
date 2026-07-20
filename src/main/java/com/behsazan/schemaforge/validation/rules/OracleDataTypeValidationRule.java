package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import com.behsazan.schemaforge.validation.domain.ValidationIssue;
import com.behsazan.schemaforge.validation.domain.ValidationSeverity;
import com.behsazan.schemaforge.validation.oracle.OracleDataTypeValidator;

import java.util.Objects;

/**
 * Validates an Oracle data type stored in the validation context.
 */
public final class OracleDataTypeValidationRule implements ValidationRule {

    public static final String ATTRIBUTE_DATA_TYPE = "dataType";
    public static final String ATTRIBUTE_COLUMN_NAME = "columnName";

    private final OracleDataTypeValidator dataTypeValidator;

    public OracleDataTypeValidationRule() {
        this(new OracleDataTypeValidator());
    }

    public OracleDataTypeValidationRule(
            OracleDataTypeValidator dataTypeValidator
    ) {
        this.dataTypeValidator = Objects.requireNonNull(
                dataTypeValidator,
                "dataTypeValidator must not be null"
        );
    }

    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");

        String dataType = context.get(ATTRIBUTE_DATA_TYPE);
        String columnName = context.get(ATTRIBUTE_COLUMN_NAME);

        try {
            dataTypeValidator.requireValid(dataType);
        } catch (IllegalArgumentException exception) {
            context.result().addIssue(
                    new ValidationIssue(
                            ValidationSeverity.ERROR,
                            resolveCode(dataType),
                            columnName,
                            exception.getMessage()
                    )
            );
        }
    }

    private ValidationCode resolveCode(String dataType) {
        return dataType == null || dataType.isBlank()
                ? ValidationCode.REQUIRED_VALUE
                : ValidationCode.UNSUPPORTED_DATA_TYPE;
    }
}