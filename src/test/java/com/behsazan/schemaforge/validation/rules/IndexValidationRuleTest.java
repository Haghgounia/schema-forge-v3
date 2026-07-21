package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.*;
import com.behsazan.schemaforge.domain.valueobject.*;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class IndexValidationRuleTest {
    @Test void shouldRejectRepeatedIndexColumn() {
        Identifier code = Identifier.of("CODE");
        Index index = new Index(Identifier.of("IX_CUSTOMERS_CODE"),
                List.of(new IndexColumn(code, null), new IndexColumn(code, null)), null, null);
        Table table = Table.builder("APP", "CUSTOMERS")
                .addColumn(Column.required("CODE", DataType.varchar("VARCHAR2", 30))).addIndex(index).build();
        ValidationContext context = context(DatabaseSchema.builder("APP").addTable(table).build());
        new IndexValidationRule().validate(context);
        assertTrue(context.result().issues().stream().anyMatch(i -> i.code() == ValidationCode.INVALID_INDEX));
    }
    private ValidationContext context(DatabaseSchema schema) { ValidationContext c = new ValidationContext(); c.put(SchemaValidationRule.ATTRIBUTE_SCHEMA, schema); return c; }
}
