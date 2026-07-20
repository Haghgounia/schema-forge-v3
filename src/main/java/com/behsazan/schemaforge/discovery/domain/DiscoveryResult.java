package com.behsazan.schemaforge.discovery.domain;

import java.util.List;

public record DiscoveryResult(List<DiscoveryIssue> issues) {

    public DiscoveryResult {
        issues = issues == null ? List.of() : List.copyOf(issues);
    }

    public long count(DiscoverySeverity severity) {
        return issues.stream().filter(issue -> issue.severity() == severity).count();
    }

    public boolean hasErrors() {
        return count(DiscoverySeverity.ERROR) > 0;
    }
}
