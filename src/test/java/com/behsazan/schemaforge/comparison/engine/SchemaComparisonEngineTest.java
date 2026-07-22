package com.behsazan.schemaforge.comparison.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.behsazan.schemaforge.comparison.column.DataTypeNameComparisonRule;
import com.behsazan.schemaforge.comparison.context.ComparisonContextFactory;
import com.behsazan.schemaforge.comparison.model.DifferenceSeverity;
import com.behsazan.schemaforge.comparison.rule.ColumnDefinitionComparisonRule;
import com.behsazan.schemaforge.comparison.rule.ColumnExistenceComparisonRule;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import java.util.List;
import org.junit.jupiter.api.Test;

class SchemaComparisonEngineTest {
    @Test
    void createsImmutableReportAndSummary() {
        SchemaComparisonEngine engine = new SchemaComparisonEngine(
                new ComparisonContextFactory(),
                List.of(new ColumnDefinitionComparisonRule(List.of(new DataTypeNameComparisonRule())),
                        new ColumnExistenceComparisonRule()));
        Table document = table(Column.required("ID", DataType.simple("NUMBER")),
                Column.required("NAME", DataType.varchar("VARCHAR2", 100)));
        Table database = table(Column.required("ID", DataType.simple("VARCHAR2")));

        var report = engine.compare(document, database);

        assertTrue(report.hasDifferences());
        assertEquals(2, report.summary().total());
        assertEquals(1, report.summary().critical());
        assertEquals(1, report.differences().stream()
                .filter(item -> item.severity() == DifferenceSeverity.HIGH).count());
    }

    private Table table(Column... columns) {
        Table.Builder builder = Table.builder("APP", "CUSTOMER");
        for (Column column : columns) builder.addColumn(column);
        return builder.build();
    }
}
