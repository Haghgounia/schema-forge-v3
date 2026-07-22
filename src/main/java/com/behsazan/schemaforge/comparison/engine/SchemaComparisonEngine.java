package com.behsazan.schemaforge.comparison.engine;

import com.behsazan.schemaforge.comparison.context.ComparisonContext;
import com.behsazan.schemaforge.comparison.context.ComparisonContextFactory;
import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.comparison.model.ComparisonSummary;
import com.behsazan.schemaforge.comparison.model.TableComparisonReport;
import com.behsazan.schemaforge.comparison.rule.ComparisonRule;
import com.behsazan.schemaforge.domain.model.Table;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public final class SchemaComparisonEngine {
    private final ComparisonContextFactory contextFactory;
    private final List<ComparisonRule> rules;

    public SchemaComparisonEngine(ComparisonContextFactory contextFactory, List<ComparisonRule> rules) {
        this.contextFactory = Objects.requireNonNull(contextFactory, "contextFactory must not be null");
        this.rules = rules.stream().sorted(Comparator.comparingInt(ComparisonRule::order)).toList();
    }

    public TableComparisonReport compare(Table documentTable, Table databaseTable) {
        ComparisonContext context = contextFactory.create(documentTable, databaseTable);
        List<ComparisonDifference> differences = new ArrayList<>();
        for (ComparisonRule rule : rules) differences.addAll(rule.compare(context));
        List<ComparisonDifference> immutable = List.copyOf(differences);
        return new TableComparisonReport(
                documentTable.qualifiedName().schema().value(),
                documentTable.qualifiedName().name().value(),
                immutable,
                ComparisonSummary.from(immutable));
    }
}
