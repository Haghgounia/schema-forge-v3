package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Validates foreign-key targets against the complete schema. */
public final class ForeignKeyValidationRule implements ValidationRule {
    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");
        DatabaseSchema schema = context.get(RuleSupport.ATTRIBUTE_SCHEMA);
        if (schema == null) return;
        schema.tables().forEach(table -> {
            Set<String> names = new HashSet<>();
            table.foreignKeys().forEach(fk -> {
                RuleSupport.validateName(context, fk.name(), "foreign key");
                if (fk.name() != null && !names.add(fk.name().normalized())) {
                    RuleSupport.addError(context, ValidationCode.DUPLICATE_CONSTRAINT,
                            fk.name().value(), "Duplicate foreign-key name in table " + table.qualifiedName() + ".");
                }
                Optional<Table> target = schema.tables().stream()
                        .filter(candidate -> candidate.qualifiedName().toString()
                                .equalsIgnoreCase(fk.referencedTable().toString()))
                        .findFirst();
                if (target.isEmpty()) {
                    RuleSupport.addError(context, ValidationCode.INVALID_REFERENCE,
                            objectName(fk.name(), table), "Referenced table does not exist: " + fk.referencedTable());
                    return;
                }
                for (Identifier referencedColumn : fk.referencedColumns()) {
                    if (target.get().columns().stream().noneMatch(c -> c.name().normalized().equals(referencedColumn.normalized()))) {
                        RuleSupport.addError(context, ValidationCode.INVALID_REFERENCE,
                                objectName(fk.name(), table), "Referenced column does not exist: "
                                        + fk.referencedTable() + "." + referencedColumn.value());
                    }
                }
            });
        });
    }

    private String objectName(Identifier name, Table table) {
        return name == null ? table.qualifiedName().toString() : name.value();
    }
}
