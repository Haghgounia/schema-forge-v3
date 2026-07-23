package com.behsazan.schemaforge.specification.normalization.check;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;


class CheckConstraintNormalizerTest {


    private final CheckConstraintNormalizer normalizer =
            new CheckConstraintNormalizer();



    @Test
    void convertsEnumeratedPersianDescriptionToInExpression() {


        String result =
                normalizer.normalize(
                        "CONTRACT_MORATORIUM_CONDITION",
                        """
                        1 : بعد از سررسید قرارداد
                        2 : در طبقات
                        """
                );


        assertThat(result)
                .isEqualTo(
                        "CONTRACT_MORATORIUM_CONDITION IN (1,2)"
                );
    }
}