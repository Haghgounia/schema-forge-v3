package com.behsazan.schemaforge.comparison.normalizer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CheckExpressionNormalizerTest {
    private final CheckExpressionNormalizer normalizer = new CheckExpressionNormalizer();

    @Test
    void normalizesWhitespaceCaseAndOuterParentheses() {
        assertEquals("STATUS IN(0,1)", normalizer.normalize(" (( status   in ( 0, 1 ) )) "));
    }

    @Test
    void preservesCaseAndWhitespaceInsideStringLiteral() {
        assertEquals("STATUS='Active User'", normalizer.normalize("status = 'Active User'"));
    }

    @Test
    void preservesEscapedQuotesInsideStringLiteral() {
        assertEquals("NAME='O''Reilly'", normalizer.normalize("name = 'O''Reilly'"));
    }
}
