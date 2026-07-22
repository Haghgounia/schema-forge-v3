package com.behsazan.schemaforge.comparison.rule.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.behsazan.schemaforge.comparison.context.ComparisonContextFactory;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.comparison.signature.IndexSignatureFactory;
import com.behsazan.schemaforge.domain.enums.IndexType;
import com.behsazan.schemaforge.domain.enums.SortDirection;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.Index;
import com.behsazan.schemaforge.domain.model.IndexColumn;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.Description;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import java.util.List;
import org.junit.jupiter.api.Test;

class IndexComparisonRuleTest {
    private final IndexComparisonRule rule = new IndexComparisonRule(new IndexSignatureFactory());
    private final ComparisonContextFactory factory = new ComparisonContextFactory();

    @Test
    void ignoresIndexNameWhenStructureMatches() {
        var result = rule.compare(factory.create(table(index("IX_DOC", IndexType.NORMAL, "CODE", SortDirection.ASC)),
                table(index("IX_DB", IndexType.NORMAL, "CODE", SortDirection.ASC))));
        assertTrue(result.isEmpty());
    }

    @Test
    void reportsChangedIndexWhenSameNameHasDifferentOrder() {
        var result = rule.compare(factory.create(table(index("IX_CODE", IndexType.NORMAL, "CODE", SortDirection.ASC)),
                table(index("IX_CODE", IndexType.NORMAL, "CODE", SortDirection.DESC))));
        assertEquals(1, result.size());
        assertEquals(DifferenceType.INDEX_CHANGED, result.getFirst().type());
    }

    @Test
    void reportsMissingAndExtraIndexes() {
        var result = rule.compare(factory.create(table(index("IX_DOC", IndexType.NORMAL, "CODE", SortDirection.ASC)),
                table(index("IX_DB", IndexType.UNIQUE, "ID", SortDirection.ASC))));
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(value -> value.type() == DifferenceType.INDEX_MISSING));
        assertTrue(result.stream().anyMatch(value -> value.type() == DifferenceType.INDEX_EXTRA));
    }

    private Table table(Index index) {
        return Table.builder("APP", "CUSTOMER")
                .addColumn(Column.required("ID", DataType.simple("NUMBER")))
                .addColumn(Column.required("CODE", DataType.simple("NUMBER")))
                .addIndex(index).build();
    }

    private Index index(String name, IndexType type, String column, SortDirection direction) {
        return new Index(Identifier.of(name), List.of(new IndexColumn(Identifier.of(column), direction)),
                type, Description.empty());
    }
}
