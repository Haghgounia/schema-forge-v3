package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import com.behsazan.schemaforge.validation.domain.ValidationIssue;
import com.behsazan.schemaforge.validation.domain.ValidationSeverity;
import com.behsazan.schemaforge.validation.spelling.SpellCheckService;
import com.behsazan.schemaforge.validation.spelling.SpellingError;
import java.util.Objects;
import java.util.stream.Collectors;

/** Reports possible column-name spelling errors and always preserves the original identifier. */
public final class ColumnNameSpellingRule implements ValidationRule {

    private final SpellCheckService spellCheckService;

    public ColumnNameSpellingRule(SpellCheckService spellCheckService) {
        this.spellCheckService = Objects.requireNonNull(spellCheckService, "spellCheckService must not be null");
    }

    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");
        DatabaseSchema schema = context.get(SchemaValidationRule.ATTRIBUTE_SCHEMA);
        if (schema == null) {
            return;
        }

        schema.tables().forEach(table -> table.columns().forEach(column -> {
            String columnName = column.name().value();
            for (SpellingError error : spellCheckService.check(columnName)) {
                String suggestions = error.suggestions().stream()
                        .map(suggestion -> suggestion.value())
                        .filter(value -> !value.isBlank())
                        .collect(Collectors.joining(", "));
                String message = "Possible spelling error: " + error.word()
                        + (error.message().isBlank() ? "" : ". " + error.message())
                        + (suggestions.isBlank() ? "" : ". Suggestions: " + suggestions)
                        + ". Original identifier is preserved.";
                context.result().addIssue(new ValidationIssue(
                        ValidationSeverity.WARNING,
                        ValidationCode.SPELLING_WARNING,
                        table.qualifiedName() + "." + columnName,
                        message));
            }
        }));
    }
}
