package com.behsazan.schemaforge.dialect.postgresql;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PostgreSqlDialectTest {
    private final PostgreSqlDialect dialect = new PostgreSqlDialect();

    @Test void exposesPostgreSqlCapabilitiesAndIdentifierRules() {
        assertEquals(DatabaseProduct.POSTGRESQL, dialect.product());
        assertEquals(63, dialect.identifierPolicy().maximumLength());
        assertEquals("customer_account", dialect.identifierPolicy().normalize("CUSTOMER_ACCOUNT"));
        assertTrue(dialect.identifierRules().isReservedWord("select"));
        assertEquals("CURRENT_TIMESTAMP", dialect.ddlSyntax().currentTimestampExpression());
    }

    @Test void mapsOracleCompatibleTypesToPostgreSqlTypes() {
        assertEquals("VARCHAR", dialect.dataTypeRules().normalize("VARCHAR2"));
        assertEquals("NUMERIC", dialect.dataTypeRules().normalize("NUMBER"));
        assertEquals("TEXT", dialect.dataTypeRules().normalize("CLOB"));
        assertEquals("BYTEA", dialect.dataTypeRules().normalize("BLOB"));
        assertTrue(dialect.dataTypeRules().supports("jsonb"));
    }
}
