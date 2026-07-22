package com.behsazan.schemaforge.comparison.rule.constraint;

import static com.behsazan.schemaforge.comparison.model.ComparisonDifferenceBuilder.difference;

import com.behsazan.schemaforge.comparison.context.ComparisonContext;
import com.behsazan.schemaforge.comparison.model.*;
import com.behsazan.schemaforge.comparison.rule.ComparisonRule;
import com.behsazan.schemaforge.comparison.signature.PrimaryKeySignatureFactory;
import com.behsazan.schemaforge.domain.model.PrimaryKey;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public final class PrimaryKeyComparisonRule implements ComparisonRule {
    private final PrimaryKeySignatureFactory signatures;

    public PrimaryKeyComparisonRule(PrimaryKeySignatureFactory signatures) { this.signatures = signatures; }
    @Override public int order() { return 300; }

    @Override
    public List<ComparisonDifference> compare(ComparisonContext context) {
        Optional<PrimaryKey> expected = context.documentTable().primaryKey();
        Optional<PrimaryKey> actual = context.databaseTable().primaryKey();
        if (expected.isEmpty() && actual.isEmpty()) return List.of();
        if (expected.isPresent() && actual.isEmpty()) return List.of(create(DifferenceType.PRIMARY_KEY_MISSING,
                DifferenceSeverity.CRITICAL, ResolutionStrategy.AUTO_FIX, signatures.create(expected.get()), "MISSING",
                "Expected primary key does not exist in database"));
        if (expected.isEmpty()) return List.of(create(DifferenceType.PRIMARY_KEY_EXTRA,
                DifferenceSeverity.HIGH, ResolutionStrategy.MANUAL_REVIEW, "MISSING", signatures.create(actual.orElseThrow()),
                "Database contains a primary key not defined in document"));

        String expectedSignature = signatures.create(expected.orElseThrow());
        String actualSignature = signatures.create(actual.orElseThrow());
        if (expectedSignature.equals(actualSignature)) return List.of();
        return List.of(create(DifferenceType.PRIMARY_KEY_CHANGED, DifferenceSeverity.CRITICAL,
                ResolutionStrategy.DROP_AND_CREATE, expectedSignature, actualSignature,
                "Primary key columns or column order differ"));
    }

    private ComparisonDifference create(DifferenceType type, DifferenceSeverity severity,
                                        ResolutionStrategy resolution, String expected, String actual, String message) {
        return difference().scope(DifferenceScope.PRIMARY_KEY).type(type).severity(severity).resolution(resolution)
                .objectName("PRIMARY_KEY").property("STRUCTURE").expected(expected).actual(actual).message(message).build();
    }
}
