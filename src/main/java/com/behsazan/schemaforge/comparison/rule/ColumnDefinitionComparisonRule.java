package com.behsazan.schemaforge.comparison.rule;

import com.behsazan.schemaforge.comparison.column.ColumnComparisonRule;
import com.behsazan.schemaforge.comparison.context.ComparisonContext;
import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.domain.model.Column;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public final class ColumnDefinitionComparisonRule implements ComparisonRule {
    private final List<ColumnComparisonRule> rules;

    public ColumnDefinitionComparisonRule(List<ColumnComparisonRule> rules) {
        this.rules = rules.stream().sorted(Comparator.comparingInt(ColumnComparisonRule::order)).toList();
    }

    @Override public int order() { return 200; }

    @Override
    public List<ComparisonDifference> compare(ComparisonContext context) {
        Map<String, Column> database = context.databaseColumns().asMap();
        List<ComparisonDifference> result = new ArrayList<>();
        context.documentColumns().asMap().forEach((name, documentColumn) -> {
            Column databaseColumn = database.get(name);
            if (databaseColumn == null) return;
            for (ColumnComparisonRule rule : rules) {
                rule.compare(documentColumn, databaseColumn).ifPresent(result::add);
            }
        });
        return List.copyOf(result);
    }
}
