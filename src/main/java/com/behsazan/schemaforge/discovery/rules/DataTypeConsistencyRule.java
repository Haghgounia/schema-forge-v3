package com.behsazan.schemaforge.discovery.rules;

import com.behsazan.schemaforge.discovery.core.DiscoveryContext;
import com.behsazan.schemaforge.discovery.core.DiscoveryRule;
import com.behsazan.schemaforge.discovery.domain.DiscoveryCategory;
import com.behsazan.schemaforge.discovery.domain.DiscoveryIssue;
import com.behsazan.schemaforge.discovery.domain.DiscoverySeverity;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.specification.domain.ColumnDefinition;
import com.behsazan.schemaforge.specification.domain.DataTypeDefinition;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DataTypeConsistencyRule implements DiscoveryRule {

    @Override
    public List<DiscoveryIssue> evaluate(DiscoveryContext context) {
        List<DiscoveryIssue> issues = new ArrayList<>();
        for (ColumnDefinition documentColumn : context.documentTable().columns()) {
            List<String> existingTypes = context.databaseSchema().tables().stream()
                    .flatMap(table -> table.findColumn(documentColumn.name()).stream())
                    .map(Column::dataType)
                    .map(DataTypeConsistencyRule::signature)
                    .toList();

            if (existingTypes.isEmpty()) {
                continue;
            }

            String standardType = mostFrequent(existingTypes);
            String documentType = signature(documentColumn.dataType());
            if (standardType.equals(documentType)) {
                continue;
            }

            Map<String, String> details = new LinkedHashMap<>();
            details.put("standardType", standardType);
            details.put("documentType", documentType);
            details.put("existingTypes", existingTypes.stream().distinct().sorted().collect(Collectors.joining(", ")));

            issues.add(new DiscoveryIssue(
                    DiscoverySeverity.WARNING,
                    DiscoveryCategory.DATA_TYPE_CONSISTENCY,
                    "COLUMN_DATA_TYPE_MISMATCH",
                    context.documentTable().schema(),
                    context.documentTable().name(),
                    documentColumn.name(),
                    "Existing standard type is " + standardType + "; document uses " + documentType + ".",
                    details));
        }
        return List.copyOf(issues);
    }

    private static String mostFrequent(List<String> values) {
        return values.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
    }

    private static String signature(DataTypeDefinition type) {
        return signature(type.name(), type.length(), type.precision(), type.scale());
    }

    private static String signature(DataType type) {
        return signature(type.name().value(), type.length(), type.precision(), type.scale());
    }

    private static String signature(String name, Integer length, Integer precision, Integer scale) {
        String normalized = name.trim().toUpperCase(Locale.ROOT);
        if (length != null) {
            return normalized + "(" + length + ")";
        }
        if (precision != null) {
            return scale == null || scale == 0
                    ? normalized + "(" + precision + ")"
                    : normalized + "(" + precision + "," + scale + ")";
        }
        return normalized;
    }
}
