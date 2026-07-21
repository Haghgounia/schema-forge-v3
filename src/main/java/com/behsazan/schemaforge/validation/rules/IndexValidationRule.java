package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Validates index names and repeated index columns. */
public final class IndexValidationRule implements ValidationRule {
    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");
        DatabaseSchema schema = context.get(RuleSupport.ATTRIBUTE_SCHEMA);
        if (schema == null) return;
        schema.tables().forEach(table -> {
            Set<String> names = new HashSet<>();
            table.indexes().forEach(index -> {
                RuleSupport.validateName(context, index.name(), "index");
                if (index.name() != null && !names.add(index.name().normalized())) {
                    RuleSupport.addError(context, ValidationCode.DUPLICATE_INDEX,
                            index.name().value(), "Duplicate index name in table " + table.qualifiedName() + ".");
                }
                List<Identifier> columns = index.columns().stream().map(c -> c.column()).toList();
                if (RuleSupport.hasDuplicates(columns)) {
                    RuleSupport.addError(context, ValidationCode.INVALID_INDEX,
                            index.name() == null ? table.qualifiedName().toString() : index.name().value(),
                            "Index contains duplicate column references.");
                }
            });
        });
    }
}
