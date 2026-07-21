package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.*;
import com.behsazan.schemaforge.domain.valueobject.*;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ForeignKeyValidationRuleTest {
    @Test void shouldRejectMissingReferencedTable() {
        ForeignKey fk = new ForeignKey(Identifier.of("FK_ORDERS_CUSTOMER"), List.of(Identifier.of("CUSTOMER_ID")),
                QualifiedName.of("APP", "CUSTOMERS"), List.of(Identifier.of("ID")), null, null);
        Table orders = Table.builder("APP", "ORDERS")
                .addColumn(Column.required("CUSTOMER_ID", DataType.numeric("NUMBER", 19, 0))).addForeignKey(fk).build();
        ValidationContext context = context(DatabaseSchema.builder("APP").addTable(orders).build());
        new ForeignKeyValidationRule().validate(context);
        assertTrue(context.result().issues().stream().anyMatch(i -> i.code() == ValidationCode.INVALID_REFERENCE));
    }
    private ValidationContext context(DatabaseSchema schema) { ValidationContext c = new ValidationContext(); c.put(SchemaValidationRule.ATTRIBUTE_SCHEMA, schema); return c; }
}
