package com.behsazan.schemaforge.comparison.normalizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DefaultValueNormalizerTest {
    @Test
    void removesOnlyBalancedOuterParentheses() {
        assertEquals("0", DefaultValueNormalizer.normalize("((0))"));
        assertEquals("COALESCE(A, 0)", DefaultValueNormalizer.normalize("COALESCE(A,  0)"));
    }

    @Test
    void preservesWhitespaceInsideStringLiteral() {
        assertEquals("'A  B'", DefaultValueNormalizer.normalize(" 'A  B' "));
    }
}
