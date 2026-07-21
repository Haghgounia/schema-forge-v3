package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;

import java.util.Objects;

/** Validates primary-key business rules not enforced by the domain model. */
public final class PrimaryKeyValidationRule implements ValidationRule {
    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");
        DatabaseSchema schema = context.get(RuleSupport.ATTRIBUTE_SCHEMA);
        if (schema == null) return;
        schema.tables().forEach(table -> table.primaryKey().ifPresent(pk -> {
            RuleSupport.validateName(context, pk.name(), "primary key");
            if (RuleSupport.hasDuplicates(pk.columns())) {
                RuleSupport.addError(context, ValidationCode.INVALID_PRIMARY_KEY,
                        pk.name() == null ? table.qualifiedName().toString() : pk.name().value(),
                        "Primary key contains duplicate column references.");
            }
        }));
    }
}
