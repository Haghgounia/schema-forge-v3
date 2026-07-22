package com.behsazan.schemaforge.comparison.column;

import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.comparison.model.DifferenceSeverity;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.domain.model.Column;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public final class DataTypeNameComparisonRule extends AbstractColumnDifferenceRule {
    @Override public int order() { return 100; }

    @Override
    public Optional<ComparisonDifference> compare(Column documentColumn, Column databaseColumn) {
        String expected = documentColumn.dataType().name().normalized();
        String actual = databaseColumn.dataType().name().normalized();
        if (expected.equals(actual)) return Optional.empty();
        return Optional.of(changed(documentColumn, DifferenceType.DATA_TYPE_CHANGED,
                DifferenceSeverity.HIGH, "DATA_TYPE", expected, actual,
                "Column data type differs"));
    }
}
