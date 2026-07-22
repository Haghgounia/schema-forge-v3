package com.behsazan.schemaforge.comparison.column;

import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.comparison.model.DifferenceSeverity;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.domain.model.Column;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public final class IdentityComparisonRule extends AbstractColumnDifferenceRule {
    @Override public int order() { return 700; }

    @Override
    public Optional<ComparisonDifference> compare(Column documentColumn, Column databaseColumn) {
        if (documentColumn.identity() == databaseColumn.identity()) return Optional.empty();
        return Optional.of(changed(documentColumn, DifferenceType.IDENTITY_CHANGED,
                DifferenceSeverity.HIGH, "IDENTITY",
                Boolean.toString(documentColumn.identity()), Boolean.toString(databaseColumn.identity()),
                "Column identity property differs"));
    }
}
