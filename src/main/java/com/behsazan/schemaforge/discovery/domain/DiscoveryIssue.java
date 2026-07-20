package com.behsazan.schemaforge.discovery.domain;

import java.util.Map;
import java.util.Objects;

public record DiscoveryIssue(
        DiscoverySeverity severity,
        DiscoveryCategory category,
        String code,
        String schemaName,
        String tableName,
        String columnName,
        String message,
        Map<String, String> details) {

    public DiscoveryIssue {
        Objects.requireNonNull(severity, "severity must not be null");
        Objects.requireNonNull(category, "category must not be null");
        Objects.requireNonNull(code, "code must not be null");
        Objects.requireNonNull(message, "message must not be null");
        details = details == null ? Map.of() : Map.copyOf(details);
    }
}
