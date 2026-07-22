package com.behsazan.schemaforge.comparison.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.behsazan.schemaforge.comparison.context.ComparisonContextFactory;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import org.junit.jupiter.api.Test;

class ColumnExistenceComparisonRuleTest {
    private final ColumnExistenceComparisonRule rule = new ColumnExistenceComparisonRule();
    private final ComparisonContextFactory factory = new ComparisonContextFactory();

    @Test
    void reportsMissingAndExtraColumns() {
        Table document = table(column("ID"), column("DOCUMENT_ONLY"));
        Table database = table(column("ID"), column("DATABASE_ONLY"));

        var result = rule.compare(factory.create(document, database));

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(item -> item.type() == DifferenceType.COLUMN_MISSING
                && item.objectName().equals("DOCUMENT_ONLY")));
        assertTrue(result.stream().anyMatch(item -> item.type() == DifferenceType.COLUMN_EXTRA
                && item.objectName().equals("DATABASE_ONLY")));
    }

    private Table table(Column... columns) {
        Table.Builder builder = Table.builder("APP", "CUSTOMER");
        for (Column column : columns) builder.addColumn(column);
        return builder.build();
    }

    private Column column(String name) { return Column.required(name, DataType.simple("NUMBER")); }
}
