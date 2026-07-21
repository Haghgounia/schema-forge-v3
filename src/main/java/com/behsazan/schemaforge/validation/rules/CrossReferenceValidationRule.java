package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;

import java.util.Objects;

/** Validates schema-wide references that are outside individual table aggregates. */
public final class CrossReferenceValidationRule implements ValidationRule {
    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");
        DatabaseSchema schema = context.get(RuleSupport.ATTRIBUTE_SCHEMA);
        if (schema == null) return;
        schema.triggers().forEach(trigger -> {
            RuleSupport.validateOracleName(context, trigger.qualifiedName().name(), "trigger");
            boolean tableExists = schema.tables().stream()
                    .anyMatch(table -> table.qualifiedName().toString().equalsIgnoreCase(trigger.table().toString()));
            if (!tableExists) {
                RuleSupport.addError(context, ValidationCode.INVALID_REFERENCE,
                        trigger.qualifiedName().toString(),
                        "Trigger references a table that does not exist: " + trigger.table());
            }
        });
    }
}
