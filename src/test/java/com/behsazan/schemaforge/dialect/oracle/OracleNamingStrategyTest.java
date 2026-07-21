package com.behsazan.schemaforge.dialect.oracle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OracleNamingStrategyTest {

    private final OracleDialect dialect = new OracleDialect();

    @Test
    void createsStableObjectNames() {
        assertEquals("PK_CUSTOMER", dialect.namingStrategy().primaryKey("customer"));
        assertEquals("FK_ACCOUNT_CUSTOMER", dialect.namingStrategy().foreignKey("account", "customer"));
        assertEquals("IX_CUSTOMER_NAME", dialect.namingStrategy().index("customer", "name"));
        assertEquals("SEQ_CUSTOMER", dialect.namingStrategy().sequence("customer"));
    }

    @Test
    void limitsGeneratedNameLength() {
        String generated = dialect.namingStrategy().trigger("A".repeat(120), "AUDIT_HISTORY");
        assertTrue(generated.length() <= dialect.identifierPolicy().maximumLength());
    }
}
