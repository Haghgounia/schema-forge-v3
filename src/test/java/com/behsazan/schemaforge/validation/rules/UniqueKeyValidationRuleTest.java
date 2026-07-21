package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.*;
import com.behsazan.schemaforge.domain.valueobject.*;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class UniqueKeyValidationRuleTest {
    @Test void shouldRejectRepeatedUniqueKeyColumn() {
        Identifier code = Identifier.of("CODE");
        Table table = Table.builder("APP", "CUSTOMERS")
                .addColumn(Column.required("CODE", DataType.varchar("VARCHAR2", 30)))
                .addUniqueKey(new UniqueKey(Identifier.of("UK_CUSTOMERS_CODE"), List.of(code, code))).build();
        ValidationContext context = context(DatabaseSchema.builder("APP").addTable(table).build());
        new UniqueKeyValidationRule().validate(context);
        assertTrue(context.result().issues().stream().anyMatch(i -> i.code() == ValidationCode.INVALID_UNIQUE_KEY));
    }
    private ValidationContext context(DatabaseSchema schema) { ValidationContext c = new ValidationContext(); c.put(SchemaValidationRule.ATTRIBUTE_SCHEMA, schema); return c; }
}
