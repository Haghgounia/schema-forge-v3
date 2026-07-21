package com.behsazan.schemaforge.specification.recovery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DataTypeNormalizerTest {
    private final DataTypeNormalizer normalizer = new DataTypeNormalizer();

    @Test void recoversKnownMalformedTypes() {
        assertEquals("NUMBER(2,1)", normalizer.normalize("NUMBER2(1)").value());
        assertEquals("NUMBER(10,0)", normalizer.normalize("NUMBER(10.0)").value());
        assertEquals("NUMBER(4)", normalizer.normalize("NUMBER(4) NUMBER(2)").value());
        assertEquals("TIMESTAMP", normalizer.normalize("TIME_STAMP").value());
        assertEquals("NUMBER(10)", normalizer.normalize("NUMBER(10(").value());
    }

    @Test void fallsBackForTextPlacedInDatatypeCell() {
        RecoveryResult result = normalizer.normalize("REMARKS");
        assertEquals("VARCHAR2(4000)", result.value());
        assertFalse(result.warnings().isEmpty());
    }
}
