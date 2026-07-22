package com.behsazan.schemaforge.comparison.column;

import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.domain.model.Column;
import java.util.Optional;

public interface ColumnComparisonRule {
    int order();
    Optional<ComparisonDifference> compare(Column documentColumn, Column databaseColumn);
}
