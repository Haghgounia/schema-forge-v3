package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.*;
import com.behsazan.schemaforge.domain.valueobject.*;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ColumnValidationRuleTest {
    @Test void shouldRejectUnsupportedOracleDataType() {
        Table table = Table.builder("APP", "CUSTOMERS")
                .addColumn(Column.required("ID", DataType.simple("MONEY"))).build();
        ValidationContext context = context(DatabaseSchema.builder("APP").addTable(table).build());
        new ColumnValidationRule().validate(context);
        assertTrue(context.result().issues().stream().anyMatch(i -> i.code() == ValidationCode.UNSUPPORTED_DATA_TYPE));
    }
    private ValidationContext context(DatabaseSchema schema) { ValidationContext c = new ValidationContext(); c.put(SchemaValidationRule.ATTRIBUTE_SCHEMA, schema); return c; }
}
