package com.behsazan.schemaforge.generation.ddl.generator.storage;

import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhysicalOptionsRendererTest {
    @Test void rendersOraclePhysicalOptionsInInsertionOrder() {
        var options = new LinkedHashMap<String,String>();
        options.put("tablespace", "DATA_TS"); options.put("pctfree", "10"); options.put("logging", "false"); options.put("compress", "true");
        assertEquals("\nTABLESPACE DATA_TS\nPCTFREE 10\nNOLOGGING\nCOMPRESS", new PhysicalOptionsRenderer().render(options, new OracleDialect()));
    }
    @Test void rejectsUnknownOracleOption() {
        assertThrows(IllegalArgumentException.class, () -> new PhysicalOptionsRenderer().render(java.util.Map.of("foo", "bar"), new OracleDialect()));
    }
}
