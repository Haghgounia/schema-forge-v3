package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** Validates unique-key names and column lists. */
public final class UniqueKeyValidationRule implements ValidationRule {
    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");
        DatabaseSchema schema = context.get(RuleSupport.ATTRIBUTE_SCHEMA);
        if (schema == null) return;
        schema.tables().forEach(table -> {
            Set<String> names = new HashSet<>();
            table.uniqueKeys().forEach(key -> {
                RuleSupport.validateOracleName(context, key.name(), "unique key");
                Identifier name = key.name();
                if (name != null && !names.add(name.normalized())) {
                    RuleSupport.addError(context, ValidationCode.DUPLICATE_CONSTRAINT,
                            name.value(), "Duplicate unique-key name in table " + table.qualifiedName() + ".");
                }
                if (RuleSupport.hasDuplicates(key.columns())) {
                    RuleSupport.addError(context, ValidationCode.INVALID_UNIQUE_KEY,
                            name == null ? table.qualifiedName().toString() : name.value(),
                            "Unique key contains duplicate column references.");
                }
            });
        });
    }
}
