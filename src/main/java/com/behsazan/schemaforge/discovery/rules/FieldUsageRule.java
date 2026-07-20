package com.behsazan.schemaforge.discovery.rules;

import com.behsazan.schemaforge.discovery.core.DiscoveryContext;
import com.behsazan.schemaforge.discovery.core.DiscoveryRule;
import com.behsazan.schemaforge.discovery.domain.DiscoveryCategory;
import com.behsazan.schemaforge.discovery.domain.DiscoveryIssue;
import com.behsazan.schemaforge.discovery.domain.DiscoverySeverity;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.specification.domain.ColumnDefinition;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FieldUsageRule implements DiscoveryRule {

    @Override
    public List<DiscoveryIssue> evaluate(DiscoveryContext context) {
        List<DiscoveryIssue> issues = new ArrayList<>();
        for (ColumnDefinition documentColumn : context.documentTable().columns()) {
            List<Table> matchingTables = context.databaseSchema().tables().stream()
                    .filter(table -> table.findColumn(documentColumn.name()).isPresent())
                    .toList();

            Map<String, String> details = new LinkedHashMap<>();
            details.put("usageCount", Integer.toString(matchingTables.size()));
            details.put("locations", matchingTables.stream()
                    .map(table -> table.qualifiedName().toString())
                    .collect(Collectors.joining(", ")));

            String message = matchingTables.isEmpty()
                    ? "Column " + documentColumn.name() + " has no existing usage."
                    : "Column " + documentColumn.name() + " is used in " + matchingTables.size() + " table(s).";

            issues.add(new DiscoveryIssue(
                    DiscoverySeverity.INFO,
                    DiscoveryCategory.FIELD_USAGE,
                    matchingTables.isEmpty() ? "FIELD_FIRST_USAGE" : "FIELD_EXISTING_USAGE",
                    context.documentTable().schema(),
                    context.documentTable().name(),
                    documentColumn.name(),
                    message,
                    details));
        }
        return List.copyOf(issues);
    }
}
