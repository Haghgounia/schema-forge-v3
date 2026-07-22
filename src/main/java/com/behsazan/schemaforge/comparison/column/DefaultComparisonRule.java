package com.behsazan.schemaforge.comparison.column;

import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.comparison.model.DifferenceSeverity;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.comparison.normalizer.DefaultValueNormalizer;
import com.behsazan.schemaforge.domain.model.Column;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public final class DefaultComparisonRule extends AbstractColumnDifferenceRule {
    @Override public int order() { return 600; }

    @Override
    public Optional<ComparisonDifference> compare(Column documentColumn, Column databaseColumn) {
        String expected = DefaultValueNormalizer.normalize(documentColumn.defaultValue().expression());
        String actual = DefaultValueNormalizer.normalize(databaseColumn.defaultValue().expression());
        if (expected.equals(actual)) return Optional.empty();
        return Optional.of(changed(documentColumn, DifferenceType.DEFAULT_CHANGED,
                DifferenceSeverity.MEDIUM, "DEFAULT", expected, actual,
                "Column default value differs"));
    }
}
