package com.behsazan.schemaforge.comparison.model;

public final class ComparisonDifferenceBuilder {
    private DifferenceScope scope;
    private DifferenceType type;
    private DifferenceSeverity severity;
    private ResolutionStrategy resolutionStrategy;
    private String objectName;
    private String property;
    private String expectedValue;
    private String actualValue;
    private String message;

    private ComparisonDifferenceBuilder() { }

    public static ComparisonDifferenceBuilder difference() { return new ComparisonDifferenceBuilder(); }
    public ComparisonDifferenceBuilder scope(DifferenceScope value) { scope = value; return this; }
    public ComparisonDifferenceBuilder type(DifferenceType value) { type = value; return this; }
    public ComparisonDifferenceBuilder severity(DifferenceSeverity value) { severity = value; return this; }
    public ComparisonDifferenceBuilder resolution(ResolutionStrategy value) { resolutionStrategy = value; return this; }
    public ComparisonDifferenceBuilder objectName(String value) { objectName = value; return this; }
    public ComparisonDifferenceBuilder property(String value) { property = value; return this; }
    public ComparisonDifferenceBuilder expected(String value) { expectedValue = value; return this; }
    public ComparisonDifferenceBuilder actual(String value) { actualValue = value; return this; }
    public ComparisonDifferenceBuilder message(String value) { message = value; return this; }

    public ComparisonDifference build() {
        return new ComparisonDifference(scope, type, severity, resolutionStrategy,
                objectName, property, expectedValue, actualValue, message);
    }
}
