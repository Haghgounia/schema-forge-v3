package com.behsazan.schemaforge.comparison.column;

import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.comparison.model.DifferenceSeverity;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.domain.model.Column;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public final class NullableComparisonRule extends AbstractColumnDifferenceRule {
    @Override public int order() { return 500; }

    @Override
    public Optional<ComparisonDifference> compare(Column documentColumn, Column databaseColumn) {
        if (documentColumn.nullable() == databaseColumn.nullable()) return Optional.empty();
        return Optional.of(changed(documentColumn, DifferenceType.NULLABLE_CHANGED,
                DifferenceSeverity.HIGH, "NULLABLE",
                Boolean.toString(documentColumn.nullable()), Boolean.toString(databaseColumn.nullable()),
                "Column nullability differs"));
    }
}
