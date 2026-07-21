package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.core.ValidationEngine;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import com.behsazan.schemaforge.validation.domain.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TableValidationRuleTest {

    @Test
    void shouldAcceptValidTableName() {
        ValidationResult result = validate(schemaWithTable("CUSTOMERS"));

        assertTrue(result.isValid());
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldRejectReservedTableName() {
        ValidationResult result = validate(schemaWithTable("TABLE"));

        assertFalse(result.isValid());
        assertEquals(1, result.size());
        assertEquals(ValidationCode.RESERVED_WORD, result.issues().getFirst().code());
    }

    @Test
    void shouldRejectTableNameBeyondOracleLimit() {
        ValidationResult result = validate(schemaWithTable("T".repeat(129)));

        assertFalse(result.isValid());
        assertEquals(1, result.size());
        assertEquals(ValidationCode.IDENTIFIER_TOO_LONG, result.issues().getFirst().code());
    }

    @Test
    void shouldIgnoreMissingSchemaBecauseSchemaRuleOwnsThatValidation() {
        ValidationResult result = validate(null);

        assertTrue(result.isValid());
        assertTrue(result.isEmpty());
    }

    @Test
    void domainModelShouldRejectInvalidTableIdentifierBeforeRuleExecution() {
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> schemaWithTable("1CUSTOMERS")
        );

        assertTrue(exception.getMessage().contains("invalid identifier"));
    }

    private DatabaseSchema schemaWithTable(String tableName) {
        Table table = Table.builder("BANKING", tableName)
                .addColumn(Column.required("ID", DataType.simple("NUMBER")))
                .build();

        return DatabaseSchema.builder("BANKING")
                .addTable(table)
                .build();
    }

    private ValidationResult validate(DatabaseSchema schema) {
        ValidationContext context = new ValidationContext();
        if (schema != null) {
            context.put(SchemaValidationRule.ATTRIBUTE_SCHEMA, schema);
        }

        return new ValidationEngine()
                .register(new TableValidationRule())
                .validate(context);
    }
}
