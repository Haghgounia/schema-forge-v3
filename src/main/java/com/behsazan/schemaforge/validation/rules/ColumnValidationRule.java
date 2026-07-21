package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;

import java.util.Objects;

/** Validates column rules using the active database dialect. */
public final class ColumnValidationRule implements ValidationRule {

    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");
        DatabaseSchema schema = context.get(RuleSupport.ATTRIBUTE_SCHEMA);
        if (schema == null) {
            return;
        }
        for (Table table : schema.tables()) {
            table.columns().forEach(column -> {
                RuleSupport.validateName(context, column.name(), "column");
                String declaration = RuleSupport.render(column.dataType());
                if (!RuleSupport.dialect(context).dataTypeRules().supports(declaration)) {
                    RuleSupport.addError(context, ValidationCode.UNSUPPORTED_DATA_TYPE,
                            table.qualifiedName() + "." + column.name().value(),
                            "Unsupported " + RuleSupport.dialect(context).name()
                                    + " data type: " + declaration);
                }
            });
        }
    }
}
