package com.behsazan.schemaforge.dialect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.dialect.standard.StandardDialect;
import java.util.List;
import org.junit.jupiter.api.Test;

class DialectRegistryTest {

    @Test
    void shouldResolveRegisteredDialect() {
        DialectRegistry registry = new DialectRegistry(List.of(new StandardDialect(), new OracleDialect()));
        assertEquals(DatabaseProduct.ORACLE, registry.require(DatabaseProduct.ORACLE).product());
    }

    @Test
    void shouldRejectDuplicateDialect() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DialectRegistry(List.of(new OracleDialect(), new OracleDialect())));
    }
}
