package com.behsazan.schemaforge.comparison.rule.constraint;

import static com.behsazan.schemaforge.comparison.model.ComparisonDifferenceBuilder.difference;

import com.behsazan.schemaforge.comparison.context.ComparisonContext;
import com.behsazan.schemaforge.comparison.model.*;
import com.behsazan.schemaforge.comparison.rule.ComparisonRule;
import com.behsazan.schemaforge.comparison.signature.CheckConstraintSignatureFactory;
import com.behsazan.schemaforge.domain.model.CheckConstraint;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class CheckConstraintComparisonRule implements ComparisonRule {
    private final CheckConstraintSignatureFactory signatures;
    public CheckConstraintComparisonRule(CheckConstraintSignatureFactory signatures) { this.signatures = signatures; }
    @Override public int order() { return 600; }

    @Override
    public List<ComparisonDifference> compare(ComparisonContext context) {
        Map<String, CheckConstraint> expected = bySignature(context.documentTable().checkConstraints());
        Map<String, CheckConstraint> actual = bySignature(context.databaseTable().checkConstraints());
        Map<String, CheckConstraint> actualByName = byName(context.databaseTable().checkConstraints());
        Set<String> handledActual = new HashSet<>();
        List<ComparisonDifference> result = new ArrayList<>();

        expected.forEach((signature, constraint) -> {
            if (actual.containsKey(signature)) {
                handledActual.add(signature);
                return;
            }
            CheckConstraint sameName = constraint.name() == null ? null : actualByName.get(constraint.name().normalized());
            if (sameName != null) {
                String actualSignature = signatures.create(sameName);
                handledActual.add(actualSignature);
                result.add(create(DifferenceType.CHECK_CHANGED, DifferenceSeverity.HIGH,
                        ResolutionStrategy.DROP_AND_CREATE, name(constraint), signature, actualSignature,
                        "Check constraint expression differs"));
            } else {
                result.add(create(DifferenceType.CHECK_MISSING, DifferenceSeverity.HIGH,
                        ResolutionStrategy.AUTO_FIX, name(constraint), signature, "MISSING",
                        "Expected check constraint does not exist in database"));
            }
        });

        actual.forEach((signature, constraint) -> {
            if (!expected.containsKey(signature) && !handledActual.contains(signature)) {
                result.add(create(DifferenceType.CHECK_EXTRA, DifferenceSeverity.MEDIUM,
                        ResolutionStrategy.MANUAL_REVIEW, name(constraint), "MISSING", signature,
                        "Database contains a check constraint not defined in document"));
            }
        });
        return List.copyOf(result);
    }

    private Map<String, CheckConstraint> bySignature(List<CheckConstraint> values) {
        return values.stream().collect(Collectors.toMap(signatures::create, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
    }
    private static Map<String, CheckConstraint> byName(List<CheckConstraint> values) {
        return values.stream().filter(value -> value.name() != null)
                .collect(Collectors.toMap(value -> value.name().normalized(), Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
    }
    private static String name(CheckConstraint value) { return value.name() == null ? "CHECK" : value.name().value(); }
    private static ComparisonDifference create(DifferenceType type, DifferenceSeverity severity,
                                               ResolutionStrategy resolution, String name, String expected,
                                               String actual, String message) {
        return difference().scope(DifferenceScope.CHECK).type(type).severity(severity).resolution(resolution)
                .objectName(name).property("EXPRESSION").expected(expected).actual(actual).message(message).build();
    }
}
