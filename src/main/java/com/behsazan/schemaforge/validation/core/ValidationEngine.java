package com.behsazan.schemaforge.validation.core;

import com.behsazan.schemaforge.validation.domain.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Executes registered validation rules.
 */
public final class ValidationEngine {

    private final List<ValidationRule> rules = new ArrayList<>();

    /**
     * Registers a validation rule.
     *
     * @param rule validation rule
     * @return this engine
     */
    public ValidationEngine register(ValidationRule rule) {
        rules.add(Objects.requireNonNull(rule, "rule must not be null"));
        return this;
    }

    /**
     * Executes all registered rules.
     *
     * @param context validation context
     * @return validation result
     */
    public ValidationResult validate(ValidationContext context) {

        Objects.requireNonNull(context, "context must not be null");

        for (ValidationRule rule : rules) {
            rule.validate(context);
        }

        return context.result();
    }

    /**
     * Returns the number of registered rules.
     */
    public int size() {
        return rules.size();
    }

    /**
     * Removes all registered rules.
     */
    public void clear() {
        rules.clear();
    }
}