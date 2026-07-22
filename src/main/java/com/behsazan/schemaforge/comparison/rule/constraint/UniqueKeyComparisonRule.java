package com.behsazan.schemaforge.comparison.rule.constraint;

import static com.behsazan.schemaforge.comparison.model.ComparisonDifferenceBuilder.difference;

import com.behsazan.schemaforge.comparison.context.ComparisonContext;
import com.behsazan.schemaforge.comparison.model.*;
import com.behsazan.schemaforge.comparison.rule.ComparisonRule;
import com.behsazan.schemaforge.comparison.signature.UniqueKeySignatureFactory;
import com.behsazan.schemaforge.domain.model.UniqueKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class UniqueKeyComparisonRule implements ComparisonRule {
    private final UniqueKeySignatureFactory signatures;
    public UniqueKeyComparisonRule(UniqueKeySignatureFactory signatures) { this.signatures = signatures; }
    @Override public int order() { return 400; }

    @Override
    public List<ComparisonDifference> compare(ComparisonContext context) {
        Map<String, UniqueKey> expected = index(context.documentTable().uniqueKeys());
        Map<String, UniqueKey> actual = index(context.databaseTable().uniqueKeys());
        List<ComparisonDifference> result = new ArrayList<>();
        expected.forEach((signature, constraint) -> {
            if (!actual.containsKey(signature)) result.add(create(DifferenceType.UNIQUE_MISSING, DifferenceSeverity.HIGH,
                    ResolutionStrategy.AUTO_FIX, name(constraint), signature, "MISSING",
                    "Expected unique constraint does not exist in database"));
        });
        actual.forEach((signature, constraint) -> {
            if (!expected.containsKey(signature)) result.add(create(DifferenceType.UNIQUE_EXTRA, DifferenceSeverity.MEDIUM,
                    ResolutionStrategy.MANUAL_REVIEW, name(constraint), "MISSING", signature,
                    "Database contains a unique constraint not defined in document"));
        });
        return List.copyOf(result);
    }

    private Map<String, UniqueKey> index(List<UniqueKey> values) {
        return values.stream().collect(Collectors.toMap(signatures::create, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
    }

    private static String name(UniqueKey value) { return value.name() == null ? "UNIQUE" : value.name().value(); }
    private static ComparisonDifference create(DifferenceType type, DifferenceSeverity severity,
                                               ResolutionStrategy resolution, String name, String expected,
                                               String actual, String message) {
        return difference().scope(DifferenceScope.UNIQUE).type(type).severity(severity).resolution(resolution)
                .objectName(name).property("STRUCTURE").expected(expected).actual(actual).message(message).build();
    }
}
