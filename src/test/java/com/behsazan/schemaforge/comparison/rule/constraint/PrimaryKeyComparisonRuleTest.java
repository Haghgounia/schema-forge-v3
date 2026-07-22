package com.behsazan.schemaforge.comparison.rule.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.behsazan.schemaforge.comparison.context.ComparisonContextFactory;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.comparison.signature.PrimaryKeySignatureFactory;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.PrimaryKey;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import java.util.List;
import org.junit.jupiter.api.Test;

class PrimaryKeyComparisonRuleTest {
    private final PrimaryKeyComparisonRule rule = new PrimaryKeyComparisonRule(new PrimaryKeySignatureFactory());
    private final ComparisonContextFactory factory = new ComparisonContextFactory();

    @Test
    void ignoresConstraintNameWhenColumnsMatch() {
        var result = rule.compare(factory.create(table(pk("PK_CUSTOMER", "ID")), table(pk("SYS_C001", "ID"))));
        assertTrue(result.isEmpty());
    }

    @Test
    void reportsChangedWhenColumnOrderDiffers() {
        var result = rule.compare(factory.create(table(pk("PK_A", "ID", "CODE")), table(pk("PK_B", "CODE", "ID"))));
        assertEquals(1, result.size());
        assertEquals(DifferenceType.PRIMARY_KEY_CHANGED, result.getFirst().type());
    }

    private Table table(PrimaryKey primaryKey) {
        return Table.builder("APP", "CUSTOMER")
                .addColumn(Column.required("ID", DataType.simple("NUMBER")))
                .addColumn(Column.required("CODE", DataType.simple("NUMBER")))
                .primaryKey(primaryKey).build();
    }
    private PrimaryKey pk(String name, String... columns) {
        return new PrimaryKey(Identifier.of(name), List.of(columns).stream().map(Identifier::of).toList());
    }
}
