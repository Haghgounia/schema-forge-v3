package com.behsazan.schemaforge.comparison.column;

import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.comparison.model.DifferenceSeverity;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.domain.model.Column;
import java.util.Optional;
import org.springframework.stereotype.Component;


@Component
public final class ScaleComparisonRule extends AbstractColumnDifferenceRule {

    @Override
    public int order() {
        return 400;
    }


    @Override
    public Optional<ComparisonDifference> compare(
            Column documentColumn,
            Column databaseColumn) {


        Integer expected =
                normalizeScale(
                        documentColumn.dataType().scale()
                );


        Integer actual =
                normalizeScale(
                        databaseColumn.dataType().scale()
                );


        if (expected.equals(actual)) {
            return Optional.empty();
        }


        return Optional.of(
                changed(
                        documentColumn,
                        DifferenceType.SCALE_CHANGED,
                        DifferenceSeverity.HIGH,
                        "SCALE",
                        value(expected),
                        value(actual),
                        "Column scale differs"
                )
        );
    }


    private Integer normalizeScale(Integer value) {
        return value == null ? 0 : value;
    }


    private String value(Integer value) {
        return value == null ? "" : value.toString();
    }
}