package com.behsazan.schemaforge.dialect.oracle;

import com.behsazan.schemaforge.dialect.DatabaseCapability;
import com.behsazan.schemaforge.dialect.LogicalDataType;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OracleDialectPolicyTest {

    private final OracleDialect dialect = new OracleDialect();

    @Test
    void exposesOracleCapabilities() {
        assertTrue(dialect.supports(DatabaseCapability.SEQUENCE));
        assertTrue(dialect.supports(DatabaseCapability.SYNONYM));
        assertFalse(dialect.capabilities().values().isEmpty());
    }

    @Test
    void validatesAndQuotesIdentifiers() {
        assertTrue(dialect.identifierPolicy().isValidUnquoted("CUSTOMER_ACCOUNT"));
        assertFalse(dialect.identifierPolicy().isValidUnquoted("1CUSTOMER"));
        assertEquals("\"Customer\"", dialect.identifierPolicy().quote("Customer"));
        assertTrue(dialect.reservedWordProvider().isReserved("select"));
    }

    @Test
    void rendersCanonicalVarcharAsOracleVarchar2() {
        assertEquals("VARCHAR2(100)", dialect.ddlGenerationPolicy()
                .renderDataType(DataType.varchar("VARCHAR", 100), dialect));
    }

    @Test
    void mapsLogicalTypesToOracleTypes() {
        assertEquals("VARCHAR2(200 CHAR)", dialect.sqlTypeMapper().map(LogicalDataType.STRING, 200, null, null));
        assertEquals("NUMBER(18,2)", dialect.sqlTypeMapper().map(LogicalDataType.DECIMAL, null, 18, 2));
        assertEquals("BLOB", dialect.sqlTypeMapper().map(LogicalDataType.LARGE_BINARY));
    }
}
