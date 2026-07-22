package com.behsazan.schemaforge.comparison.model;

import java.util.List;
import java.util.Objects;

public record TableComparisonReport(
        String schemaName,
        String tableName,
        List<ComparisonDifference> differences,
        ComparisonSummary summary) {

    public TableComparisonReport {
        schemaName = schemaName == null ? "" : schemaName;
        tableName = Objects.requireNonNull(tableName, "tableName must not be null");
        differences = List.copyOf(Objects.requireNonNull(differences, "differences must not be null"));
        summary = summary == null ? ComparisonSummary.from(differences) : summary;
    }

    public boolean hasDifferences() {
        return !differences.isEmpty();
    }
}
