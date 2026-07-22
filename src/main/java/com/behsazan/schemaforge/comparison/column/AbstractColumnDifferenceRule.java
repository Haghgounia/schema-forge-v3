package com.behsazan.schemaforge.comparison.column;

import static com.behsazan.schemaforge.comparison.model.ComparisonDifferenceBuilder.difference;

import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.comparison.model.DifferenceScope;
import com.behsazan.schemaforge.comparison.model.DifferenceSeverity;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.comparison.model.ResolutionStrategy;
import com.behsazan.schemaforge.domain.model.Column;

abstract class AbstractColumnDifferenceRule implements ColumnComparisonRule {
    protected ComparisonDifference changed(
            Column documentColumn,
            DifferenceType type,
            DifferenceSeverity severity,
            String property,
            String expected,
            String actual,
            String message) {
        return difference()
                .scope(DifferenceScope.COLUMN)
                .type(type)
                .severity(severity)
                .resolution(ResolutionStrategy.MANUAL_REVIEW)
                .objectName(documentColumn.name().value())
                .property(property)
                .expected(expected)
                .actual(actual)
                .message(message)
                .build();
    }
}
