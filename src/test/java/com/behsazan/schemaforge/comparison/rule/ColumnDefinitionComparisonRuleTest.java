package com.behsazan.schemaforge.comparison.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.behsazan.schemaforge.comparison.column.CommentComparisonRule;
import com.behsazan.schemaforge.comparison.column.DataTypeNameComparisonRule;
import com.behsazan.schemaforge.comparison.column.DefaultComparisonRule;
import com.behsazan.schemaforge.comparison.column.IdentityComparisonRule;
import com.behsazan.schemaforge.comparison.column.LengthComparisonRule;
import com.behsazan.schemaforge.comparison.column.NullableComparisonRule;
import com.behsazan.schemaforge.comparison.column.PrecisionComparisonRule;
import com.behsazan.schemaforge.comparison.column.ScaleComparisonRule;
import com.behsazan.schemaforge.comparison.context.ComparisonContextFactory;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.DefaultValue;
import com.behsazan.schemaforge.domain.valueobject.Description;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import java.util.List;
import org.junit.jupiter.api.Test;

class ColumnDefinitionComparisonRuleTest {
    private final ColumnDefinitionComparisonRule rule = new ColumnDefinitionComparisonRule(List.of(
            new DataTypeNameComparisonRule(), new LengthComparisonRule(),
            new PrecisionComparisonRule(), new ScaleComparisonRule(),
            new NullableComparisonRule(), new DefaultComparisonRule(),
            new IdentityComparisonRule(), new CommentComparisonRule()));

    @Test
    void comparesAllPropertiesOfCommonColumn() {
        Column documentColumn = new Column(Identifier.of("AMOUNT"), DataType.numeric("NUMBER", 18, 2),
                false, new DefaultValue("(0)"), new Description("Document amount"), true, 1);
        Column databaseColumn = new Column(Identifier.of("AMOUNT"), DataType.numeric("DECIMAL", 20, 4),
                true, new DefaultValue("1"), new Description("Database amount"), false, 1);

        var result = rule.compare(new ComparisonContextFactory().create(
                table(documentColumn), table(databaseColumn)));

        assertEquals(7, result.size());
        assertTrue(result.stream().anyMatch(item -> item.type() == DifferenceType.DATA_TYPE_CHANGED));
        assertTrue(result.stream().anyMatch(item -> item.type() == DifferenceType.PRECISION_CHANGED));
        assertTrue(result.stream().anyMatch(item -> item.type() == DifferenceType.SCALE_CHANGED));
        assertTrue(result.stream().anyMatch(item -> item.type() == DifferenceType.NULLABLE_CHANGED));
        assertTrue(result.stream().anyMatch(item -> item.type() == DifferenceType.DEFAULT_CHANGED));
        assertTrue(result.stream().anyMatch(item -> item.type() == DifferenceType.IDENTITY_CHANGED));
        assertTrue(result.stream().anyMatch(item -> item.type() == DifferenceType.COMMENT_CHANGED));
    }

    @Test
    void treatsParenthesizedDefaultAsEqual() {
        Column documentColumn = new Column(Identifier.of("STATUS"), DataType.simple("NUMBER"),
                false, new DefaultValue("((0))"), Description.empty(), false, 1);
        Column databaseColumn = new Column(Identifier.of("STATUS"), DataType.simple("NUMBER"),
                false, new DefaultValue("0"), Description.empty(), false, 1);

        var result = rule.compare(new ComparisonContextFactory().create(
                table(documentColumn), table(databaseColumn)));

        assertTrue(result.isEmpty());
    }

    private Table table(Column column) {
        return Table.builder("APP", "ACCOUNT").addColumn(column).build();
    }
}
