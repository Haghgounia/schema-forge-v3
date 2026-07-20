package com.behsazan.schemaforge.validation.core;

/**
 * Defines a validation rule that can be executed by the validation engine.
 */
@FunctionalInterface
public interface ValidationRule {

    /**
     * Executes the validation rule using the supplied context.
     *
     * @param context shared validation context
     */
    void validate(ValidationContext context);
}