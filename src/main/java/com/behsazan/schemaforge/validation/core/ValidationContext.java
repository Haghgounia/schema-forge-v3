package com.behsazan.schemaforge.validation.core;

import com.behsazan.schemaforge.validation.domain.ValidationResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Shared context for validation execution.
 */
public final class ValidationContext {

    private final ValidationResult result = new ValidationResult();

    private final Map<String, Object> attributes = new HashMap<>();

    public ValidationResult result() {
        return result;
    }

    public void put(String key, Object value) {
        Objects.requireNonNull(key, "key must not be null");
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) attributes.get(key);
    }

    public boolean contains(String key) {
        return attributes.containsKey(key);
    }

    public void remove(String key) {
        attributes.remove(key);
    }

    public void clear() {
        attributes.clear();
    }
}