package com.behsazan.schemaforge.comparison.model;

import java.util.List;

public record ComparisonSummary(int info, int low, int medium, int high, int critical, int total) {

    public static ComparisonSummary from(List<ComparisonDifference> differences) {
        int info = 0;
        int low = 0;
        int medium = 0;
        int high = 0;
        int critical = 0;
        for (ComparisonDifference difference : differences) {
            switch (difference.severity()) {
                case INFO -> info++;
                case LOW -> low++;
                case MEDIUM -> medium++;
                case HIGH -> high++;
                case CRITICAL -> critical++;
            }
        }
        return new ComparisonSummary(info, low, medium, high, critical, differences.size());
    }

    public boolean hasCriticalDifferences() {
        return critical > 0;
    }
}
