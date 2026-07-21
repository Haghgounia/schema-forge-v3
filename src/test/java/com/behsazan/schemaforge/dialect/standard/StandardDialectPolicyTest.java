package com.behsazan.schemaforge.dialect.standard;

import com.behsazan.schemaforge.dialect.DatabaseCapability;
import com.behsazan.schemaforge.dialect.LogicalDataType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardDialectPolicyTest {

    private final StandardDialect dialect = new StandardDialect();

    @Test
    void exposesConservativeStandardCapabilities() {
        assertTrue(dialect.supports(DatabaseCapability.IDENTITY));
        assertFalse(dialect.supports(DatabaseCapability.SYNONYM));
    }

    @Test
    void mapsStandardSqlTypes() {
        assertEquals("VARCHAR(80)", dialect.sqlTypeMapper().map(LogicalDataType.STRING, 80, null, null));
        assertEquals("BOOLEAN", dialect.sqlTypeMapper().map(LogicalDataType.BOOLEAN));
        assertEquals("DECIMAL(12,3)", dialect.sqlTypeMapper().map(LogicalDataType.DECIMAL, null, 12, 3));
    }
}
