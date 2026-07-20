package com.behsazan.schemaforge.discovery.rules;

import com.behsazan.schemaforge.discovery.core.DiscoveryContext;
import com.behsazan.schemaforge.discovery.core.DiscoveryRule;
import com.behsazan.schemaforge.discovery.domain.DiscoveryCategory;
import com.behsazan.schemaforge.discovery.domain.DiscoveryIssue;
import com.behsazan.schemaforge.discovery.domain.DiscoverySeverity;
import com.behsazan.schemaforge.specification.domain.ColumnDefinition;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NullableConsistencyRule implements DiscoveryRule {

    @Override
    public List<DiscoveryIssue> evaluate(DiscoveryContext context) {
        List<DiscoveryIssue> issues = new ArrayList<>();
        for (ColumnDefinition documentColumn : context.documentTable().columns()) {
            var usages = context.snapshot().findColumnUsage(documentColumn.name());
            List<Boolean> existingValues = usages.stream()
                    .map(usage -> usage.column().nullable())
                    .toList();
            if (existingValues.isEmpty()) {
                continue;
            }

            Boolean standardNullable = ConsistencyRuleSupport.mostFrequent(existingValues);
            if (standardNullable == documentColumn.nullable()) {
                continue;
            }

            Map<String, String> details = new LinkedHashMap<>();
            details.put("standardNullable", standardNullable.toString());
            details.put("documentNullable", Boolean.toString(documentColumn.nullable()));
            details.put("locations", ConsistencyRuleSupport.locations(usages));

            issues.add(new DiscoveryIssue(
                    DiscoverySeverity.WARNING,
                    DiscoveryCategory.NULLABLE_CONSISTENCY,
                    "COLUMN_NULLABLE_MISMATCH",
                    context.documentTable().schema(),
                    context.documentTable().name(),
                    documentColumn.name(),
                    "Existing nullable standard is " + standardNullable
                            + "; document uses " + documentColumn.nullable() + ".",
                    details));
        }
        return List.copyOf(issues);
    }
}
