package com.behsazan.schemaforge.generation.core;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.behsazan.schemaforge.generation.spi.DatabaseType;
import java.util.List;
import org.junit.jupiter.api.Test;

class DialectRegistryTest {
    @Test
    void reportsMissingDialectClearly() {
        DialectRegistry registry = new DialectRegistry(List.of());
        assertThrows(IllegalArgumentException.class, () -> registry.require(DatabaseType.ORACLE));
    }
}
