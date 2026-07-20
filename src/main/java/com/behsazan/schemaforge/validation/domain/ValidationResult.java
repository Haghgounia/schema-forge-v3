package com.behsazan.schemaforge.validation.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Collects validation issues generated during a validation process.
 */
public final class ValidationResult {

    private final List<ValidationIssue> issues = new ArrayList<>();

    public void addIssue(ValidationIssue issue) {
        issues.add(Objects.requireNonNull(issue, "issue must not be null"));
    }

    public void addAll(List<ValidationIssue> newIssues) {
        Objects.requireNonNull(newIssues, "newIssues must not be null");

        for (ValidationIssue issue : newIssues) {
            addIssue(issue);
        }
    }

    public List<ValidationIssue> issues() {
        return Collections.unmodifiableList(issues);
    }

    public boolean hasErrors() {
        return issues.stream()
                .anyMatch(ValidationIssue::isError);
    }

    public boolean hasWarnings() {
        return issues.stream()
                .anyMatch(ValidationIssue::isWarning);
    }

    public boolean hasInfo() {
        return issues.stream()
                .anyMatch(ValidationIssue::isInfo);
    }

    public boolean isValid() {
        return !hasErrors();
    }

    public boolean isEmpty() {
        return issues.isEmpty();
    }

    public int size() {
        return issues.size();
    }

    public long errorCount() {
        return issues.stream()
                .filter(ValidationIssue::isError)
                .count();
    }

    public long warningCount() {
        return issues.stream()
                .filter(ValidationIssue::isWarning)
                .count();
    }

    public long infoCount() {
        return issues.stream()
                .filter(ValidationIssue::isInfo)
                .count();
    }
}