package com.behsazan.schemaforge.specification.normalization.range;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumericRangeParserTest {
    private final NumericRangeParser parser = new NumericRangeParser();

    @Test
    void extractsEnumerationFromPersianDescriptions() {
        assertThat(parser.toCheckExpression(
                "EXCHANGE_PERIOD",
                "1: روزانه 2: هفتگی 3: ماهیانه"))
                .contains("EXCHANGE_PERIOD IN (1, 2, 3)");
    }

    @Test
    void extractsIntervalAndIgnoresPersianDescription() {
        assertThat(parser.toCheckExpression(
                "EXCHANGE_DAY_OF_WEEK",
                "۱ تا ۷ (شنبه تا جمعه)"))
                .contains("EXCHANGE_DAY_OF_WEEK BETWEEN 1 AND 7");
    }

    @Test
    void extractsEnumerationFromEnglishDescriptions() {
        assertThat(parser.toCheckExpression("IS_ACTIVE", "active:1 Inactive:0"))
                .contains("IS_ACTIVE IN (0, 1)");
    }

    @Test
    void extractsEnumerationWhenDocxSplitsDescriptionsAroundDigits() {
        assertThat(parser.toCheckExpression(
                "EXCHANGE_PERIOD",
                "روزان1:ه هفتگ2:ی ماهیان3:ه"))
                .contains("EXCHANGE_PERIOD IN (1, 2, 3)");
    }

    @Test
    void extractsBooleanEnumerationWhenEnglishWordsAreSplitAroundDigits() {
        assertThat(parser.toCheckExpression("IS_ACTIVE", "active:1 Inacti0:ve"))
                .contains("IS_ACTIVE IN (0, 1)");
    }

    @Test
    void doesNotCreateConstraintFromOneIsolatedNumber() {
        assertThat(parser.toCheckExpression("DEFAULT_LIKE_CELL", "1"))
                .isEmpty();
    }
}
