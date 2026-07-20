package com.behsazan.schemaforge.validation.domain;

import java.util.Objects;

/**
 * Represents a single validation issue.
 */
public record ValidationIssue(

        ValidationSeverity severity,

        ValidationCode code,

        String objectName,

        String message

) {

    public ValidationIssue {
        Objects.requireNonNull(severity, "severity must not be null");
        Objects.requireNonNull(code, "code must not be null");

        objectName = objectName == null ? "" : objectName.trim();
        message = message == null ? "" : message.trim();
    }

    public boolean isError() {
        return severity == ValidationSeverity.ERROR;
    }

    public boolean isWarning() {
        return severity == ValidationSeverity.WARNING;
    }

    public boolean isInfo() {
        return severity == ValidationSeverity.INFO;
    }

    @Override
    public String toString() {
        return "[" + severity + "] "
                + code
                + (objectName.isBlank() ? "" : " (" + objectName + ")")
                + " : "
                + message;
    }
}