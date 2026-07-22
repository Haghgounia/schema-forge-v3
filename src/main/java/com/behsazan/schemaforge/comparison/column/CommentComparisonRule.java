package com.behsazan.schemaforge.comparison.column;

import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.comparison.model.DifferenceSeverity;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.comparison.normalizer.TextNormalizer;
import com.behsazan.schemaforge.domain.model.Column;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public final class CommentComparisonRule extends AbstractColumnDifferenceRule {
    @Override public int order() { return 800; }

    @Override
    public Optional<ComparisonDifference> compare(Column documentColumn, Column databaseColumn) {
        String expected = TextNormalizer.normalize(documentColumn.description().value());
        String actual = TextNormalizer.normalize(databaseColumn.description().value());
        if (expected.equals(actual)) return Optional.empty();
        return Optional.of(changed(documentColumn, DifferenceType.COMMENT_CHANGED,
                DifferenceSeverity.LOW, "COMMENT",
                documentColumn.description().value(), databaseColumn.description().value(),
                "Column comment differs"));
    }
}
