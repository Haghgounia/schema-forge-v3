package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import com.behsazan.schemaforge.validation.oracle.OracleDataTypeValidator;

import java.util.Objects;

/** Validates Oracle-specific column rules. */
public final class ColumnValidationRule implements ValidationRule {
    private final OracleDataTypeValidator dataTypeValidator;

    public ColumnValidationRule() {
        this(new OracleDataTypeValidator());
    }

    ColumnValidationRule(OracleDataTypeValidator dataTypeValidator) {
        this.dataTypeValidator = Objects.requireNonNull(dataTypeValidator);
    }

    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");
        DatabaseSchema schema = context.get(RuleSupport.ATTRIBUTE_SCHEMA);
        if (schema == null) {
            return;
        }
        for (Table table : schema.tables()) {
            table.columns().forEach(column -> {
                RuleSupport.validateOracleName(context, column.name(), "column");
                try {
                    dataTypeValidator.requireValid(RuleSupport.render(column.dataType()));
                } catch (IllegalArgumentException exception) {
                    RuleSupport.addError(context, ValidationCode.UNSUPPORTED_DATA_TYPE,
                            table.qualifiedName() + "." + column.name().value(), exception.getMessage());
                }
            });
        }
    }
}
