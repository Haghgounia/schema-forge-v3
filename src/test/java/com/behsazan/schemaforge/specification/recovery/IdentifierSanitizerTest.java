package com.behsazan.schemaforge.specification.recovery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdentifierSanitizerTest {
    @Test void sanitizesCommonDocxIdentifierDefects() {
        IdentifierSanitizer sanitizer = new IdentifierSanitizer();
        assertEquals("SECURITIES_PACKING", sanitizer.sanitize("SECURITIES-PACKING", "table").value());
        assertEquals("NOF_CASH_CLOSING", sanitizer.sanitize("NOF_CASH_CLOSINGحذفشود", "column").value());
    }
}
