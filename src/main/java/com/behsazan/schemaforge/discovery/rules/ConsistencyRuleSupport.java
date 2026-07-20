package com.behsazan.schemaforge.discovery.rules;

import com.behsazan.schemaforge.discovery.snapshot.ColumnUsage;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

final class ConsistencyRuleSupport {

    private ConsistencyRuleSupport() {
    }

    static <T extends Comparable<? super T>> T mostFrequent(List<T> values) {
        return values.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<T, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
    }

    static String locations(List<ColumnUsage> usages) {
        return usages.stream()
                .map(usage -> usage.table().qualifiedName().toString())
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));
    }
}
