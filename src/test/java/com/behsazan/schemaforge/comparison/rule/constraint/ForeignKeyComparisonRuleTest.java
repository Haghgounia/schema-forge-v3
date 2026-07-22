package com.behsazan.schemaforge.comparison.rule.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.behsazan.schemaforge.comparison.context.ComparisonContextFactory;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.comparison.signature.ForeignKeySignatureFactory;
import com.behsazan.schemaforge.domain.enums.ReferentialAction;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.ForeignKey;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.domain.valueobject.QualifiedName;
import java.util.List;
import org.junit.jupiter.api.Test;

class ForeignKeyComparisonRuleTest {
    private final ForeignKeyComparisonRule rule = new ForeignKeyComparisonRule(new ForeignKeySignatureFactory());
    private final ComparisonContextFactory factory = new ComparisonContextFactory();

    @Test
    void ignoresConstraintNameWhenStructureMatches() {
        assertTrue(rule.compare(factory.create(table(fk("FK_DOC", ReferentialAction.CASCADE)),
                table(fk("SYS_FK", ReferentialAction.CASCADE)))).isEmpty());
    }

    @Test
    void reportsChangedWhenDeleteActionDiffers() {
        var result = rule.compare(factory.create(table(fk("FK_DOC", ReferentialAction.CASCADE)),
                table(fk("FK_DB", ReferentialAction.NO_ACTION))));
        assertEquals(1, result.size());
        assertEquals(DifferenceType.FOREIGN_KEY_CHANGED, result.getFirst().type());
    }

    private Table table(ForeignKey foreignKey) {
        return Table.builder("APP", "ORDERS")
                .addColumn(Column.required("CUSTOMER_ID", DataType.simple("NUMBER")))
                .addForeignKey(foreignKey).build();
    }
    private ForeignKey fk(String name, ReferentialAction deleteAction) {
        return new ForeignKey(Identifier.of(name), List.of(Identifier.of("CUSTOMER_ID")),
                QualifiedName.of("APP", "CUSTOMERS"), List.of(Identifier.of("ID")),
                deleteAction, ReferentialAction.NO_ACTION);
    }
}
