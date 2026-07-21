package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Sequence;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationRule;
import com.behsazan.schemaforge.validation.domain.ValidationCode;

import java.util.Objects;

/** Validates Oracle sequence ranges and names. */
public final class SequenceValidationRule implements ValidationRule {
    @Override
    public void validate(ValidationContext context) {
        Objects.requireNonNull(context, "context must not be null");
        DatabaseSchema schema = context.get(RuleSupport.ATTRIBUTE_SCHEMA);
        if (schema == null) return;
        schema.sequences().forEach(sequence -> {
            RuleSupport.validateName(context, sequence.qualifiedName().name(), "sequence");
            if (!hasValidRange(sequence)) {
                RuleSupport.addError(context, ValidationCode.INVALID_SEQUENCE,
                        sequence.qualifiedName().toString(),
                        "Sequence minimum, maximum and start values are inconsistent.");
            }
        });
    }

    private boolean hasValidRange(Sequence sequence) {
        Long min = sequence.minValue();
        Long max = sequence.maxValue();
        if (min != null && max != null && min > max) return false;
        if (min != null && sequence.startWith() < min) return false;
        return max == null || sequence.startWith() <= max;
    }
}
