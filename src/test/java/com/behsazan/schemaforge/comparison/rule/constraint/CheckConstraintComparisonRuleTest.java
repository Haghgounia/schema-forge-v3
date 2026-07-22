package com.behsazan.schemaforge.comparison.rule.constraint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.behsazan.schemaforge.comparison.context.ComparisonContextFactory;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.comparison.signature.CheckConstraintSignatureFactory;
import com.behsazan.schemaforge.domain.model.CheckConstraint;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import org.junit.jupiter.api.Test;

class CheckConstraintComparisonRuleTest {
    private final CheckConstraintComparisonRule rule = new CheckConstraintComparisonRule(new CheckConstraintSignatureFactory());
    private final ComparisonContextFactory factory = new ComparisonContextFactory();

    @Test
    void ignoresNameAndFormattingWhenExpressionMatches() {
        assertTrue(rule.compare(factory.create(table(check("CHK_DOC", "status in (0, 1)")),
                table(check("SYS_C001", "(( STATUS IN(0,1) ))")))).isEmpty());
    }

    @Test
    void reportsChangedWhenSameNamedExpressionDiffers() {
        var result = rule.compare(factory.create(table(check("CHK_STATUS", "STATUS IN (0,1)")),
                table(check("CHK_STATUS", "STATUS IN (0,1,2)"))));
        assertEquals(1, result.size());
        assertEquals(DifferenceType.CHECK_CHANGED, result.getFirst().type());
    }

    private Table table(CheckConstraint constraint) {
        return Table.builder("APP", "CUSTOMER")
                .addColumn(Column.required("STATUS", DataType.simple("NUMBER")))
                .addCheck(constraint).build();
    }
    private CheckConstraint check(String name, String expression) {
        return new CheckConstraint(Identifier.of(name), expression);
    }
}
