package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.*;
import com.behsazan.schemaforge.domain.valueobject.*;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class PrimaryKeyValidationRuleTest {
    @Test void shouldRejectRepeatedPrimaryKeyColumn() {
        Identifier id = Identifier.of("ID");
        Table table = Table.builder("APP", "CUSTOMERS")
                .addColumn(Column.required("ID", DataType.numeric("NUMBER", 19, 0)))
                .primaryKey(new PrimaryKey(Identifier.of("PK_CUSTOMERS"), List.of(id, id))).build();
        ValidationContext context = context(DatabaseSchema.builder("APP").addTable(table).build());
        new PrimaryKeyValidationRule().validate(context);
        assertTrue(context.result().issues().stream().anyMatch(i -> i.code() == ValidationCode.INVALID_PRIMARY_KEY));
    }
    private ValidationContext context(DatabaseSchema schema) { ValidationContext c = new ValidationContext(); c.put(SchemaValidationRule.ATTRIBUTE_SCHEMA, schema); return c; }
}
