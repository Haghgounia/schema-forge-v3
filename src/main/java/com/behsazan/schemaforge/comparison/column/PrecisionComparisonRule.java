package com.behsazan.schemaforge.comparison.column;

import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.comparison.model.DifferenceSeverity;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.domain.model.Column;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public final class PrecisionComparisonRule extends AbstractColumnDifferenceRule {
    @Override public int order() { return 300; }

    @Override
    public Optional<ComparisonDifference> compare(Column documentColumn, Column databaseColumn) {
        Integer expected = documentColumn.dataType().precision();
        Integer actual = databaseColumn.dataType().precision();
        if (Objects.equals(expected, actual)) return Optional.empty();
        return Optional.of(changed(documentColumn, DifferenceType.PRECISION_CHANGED,
                DifferenceSeverity.HIGH, "PRECISION", value(expected), value(actual),
                "Column precision differs"));
    }

    private String value(Integer value) { return value == null ? "" : value.toString(); }
}
