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
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DefaultConsistencyRule implements DiscoveryRule {

    private static final String NO_DEFAULT = "<NONE>";

    @Override
    public List<DiscoveryIssue> evaluate(DiscoveryContext context) {
        List<DiscoveryIssue> issues = new ArrayList<>();
        for (ColumnDefinition documentColumn : context.documentTable().columns()) {
            var usages = context.snapshot().findColumnUsage(documentColumn.name());
            List<String> existingDefaults = usages.stream()
                    .map(usage -> normalize(usage.column().defaultValue().expression()))
                    .toList();
            if (existingDefaults.isEmpty()) {
                continue;
            }

            String standardDefault = ConsistencyRuleSupport.mostFrequent(existingDefaults);
            String documentDefault = normalize(documentColumn.defaultValue());
            if (standardDefault.equals(documentDefault)) {
                continue;
            }

            Map<String, String> details = new LinkedHashMap<>();
            details.put("standardDefault", display(standardDefault));
            details.put("documentDefault", display(documentDefault));
            details.put("locations", ConsistencyRuleSupport.locations(usages));

            issues.add(new DiscoveryIssue(
                    DiscoverySeverity.WARNING,
                    DiscoveryCategory.DEFAULT_VALUE_CONSISTENCY,
                    "COLUMN_DEFAULT_MISMATCH",
                    context.documentTable().schema(),
                    context.documentTable().name(),
                    documentColumn.name(),
                    "Existing default standard is " + display(standardDefault)
                            + "; document uses " + display(documentDefault) + ".",
                    details));
        }
        return List.copyOf(issues);
    }

    private static String normalize(String expression) {
        if (expression == null || expression.isBlank()) {
            return NO_DEFAULT;
        }
        return expression.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }

    private static String display(String value) {
        return NO_DEFAULT.equals(value) ? "none" : value;
    }
}
