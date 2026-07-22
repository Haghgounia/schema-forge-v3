package com.behsazan.schemaforge.comparison.rule.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.behsazan.schemaforge.comparison.context.ComparisonContextFactory;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.comparison.signature.UniqueKeySignatureFactory;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.model.UniqueKey;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import java.util.List;
import org.junit.jupiter.api.Test;

class UniqueKeyComparisonRuleTest {
    private final UniqueKeyComparisonRule rule = new UniqueKeyComparisonRule(new UniqueKeySignatureFactory());
    private final ComparisonContextFactory factory = new ComparisonContextFactory();

    @Test
    void ignoresConstraintNameWhenStructureMatches() {
        assertTrue(rule.compare(factory.create(table(uk("UK_DOC", "CODE")), table(uk("UK_DB", "CODE")))).isEmpty());
    }

    @Test
    void reportsMissingAndExtraUniqueKeys() {
        var result = rule.compare(factory.create(table(uk("UK_DOC", "CODE")), table(uk("UK_DB", "ID"))));
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(value -> value.type() == DifferenceType.UNIQUE_MISSING));
        assertTrue(result.stream().anyMatch(value -> value.type() == DifferenceType.UNIQUE_EXTRA));
    }

    private Table table(UniqueKey key) {
        return Table.builder("APP", "CUSTOMER")
                .addColumn(Column.required("ID", DataType.simple("NUMBER")))
                .addColumn(Column.required("CODE", DataType.simple("NUMBER")))
                .addUniqueKey(key).build();
    }
    private UniqueKey uk(String name, String... columns) {
        return new UniqueKey(Identifier.of(name), List.of(columns).stream().map(Identifier::of).toList());
    }
}
