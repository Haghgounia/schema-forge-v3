package com.behsazan.schemaforge.dialect.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import org.junit.jupiter.api.Test;

class OracleDialectTest {

    private final OracleDialect dialect = new OracleDialect();

    @Test
    void shouldExposeOracleCapabilities() {
        assertEquals(DatabaseProduct.ORACLE, dialect.product());
        assertEquals("SYSTIMESTAMP", dialect.ddlSyntax().currentTimestampExpression());
        assertTrue(dialect.ddlSyntax().supportsCreateSequence());
    }

    @Test
    void shouldValidateOracleIdentifiers() {
        assertTrue(dialect.identifierRules().isValidUnquotedIdentifier("CUSTOMER_ACCOUNT"));
        assertFalse(dialect.identifierRules().isValidUnquotedIdentifier("1CUSTOMER"));
        assertTrue(dialect.identifierRules().isReservedWord("select"));
    }

    @Test
    void shouldRecognizeOracleDataTypes() {
        assertTrue(dialect.dataTypeRules().supports("varchar2"));
        assertTrue(dialect.dataTypeRules().supports("timestamp   with time zone"));
        assertFalse(dialect.dataTypeRules().supports("jsonb"));
    }
}
