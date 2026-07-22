package com.behsazan.schemaforge.comparison.model;

import java.util.Objects;

public record ComparisonDifference(
        DifferenceScope scope,
        DifferenceType type,
        DifferenceSeverity severity,
        ResolutionStrategy resolutionStrategy,
        String objectName,
        String property,
        String expectedValue,
        String actualValue,
        String message) {

    public ComparisonDifference {
        Objects.requireNonNull(scope, "scope must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(severity, "severity must not be null");
        resolutionStrategy = resolutionStrategy == null
                ? ResolutionStrategy.MANUAL_REVIEW : resolutionStrategy;
        objectName = safe(objectName);
        property = safe(property);
        expectedValue = safe(expectedValue);
        actualValue = safe(actualValue);
        message = safe(message);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
