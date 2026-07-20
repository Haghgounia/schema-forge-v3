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
public class LengthConsistencyRule implements DiscoveryRule {

    @Override
    public List<DiscoveryIssue> evaluate(DiscoveryContext context) {
        List<DiscoveryIssue> issues = new ArrayList<>();
        for (ColumnDefinition documentColumn : context.documentTable().columns()) {
            Integer documentLength = documentColumn.dataType().length();
            if (documentLength == null) {
                continue;
            }

            var usages = context.snapshot().findColumnUsage(documentColumn.name());
            List<Integer> existingLengths = usages.stream()
                    .map(usage -> usage.column().dataType().length())
                    .filter(length -> length != null)
                    .toList();
            if (existingLengths.isEmpty()) {
                continue;
            }

            Integer standardLength = ConsistencyRuleSupport.mostFrequent(existingLengths);
            if (standardLength.equals(documentLength)) {
                continue;
            }

            Map<String, String> details = new LinkedHashMap<>();
            details.put("standardLength", standardLength.toString());
            details.put("documentLength", documentLength.toString());
            details.put("locations", ConsistencyRuleSupport.locations(usages));

            issues.add(new DiscoveryIssue(
                    DiscoverySeverity.WARNING,
                    DiscoveryCategory.LENGTH_CONSISTENCY,
                    "COLUMN_LENGTH_MISMATCH",
                    context.documentTable().schema(),
                    context.documentTable().name(),
                    documentColumn.name(),
                    "Existing standard length is " + standardLength + "; document uses " + documentLength + ".",
                    details));
        }
        return List.copyOf(issues);
    }
}
