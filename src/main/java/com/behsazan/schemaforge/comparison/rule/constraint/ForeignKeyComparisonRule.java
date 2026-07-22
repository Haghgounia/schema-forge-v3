package com.behsazan.schemaforge.comparison.rule.constraint;

import static com.behsazan.schemaforge.comparison.model.ComparisonDifferenceBuilder.difference;

import com.behsazan.schemaforge.comparison.context.ComparisonContext;
import com.behsazan.schemaforge.comparison.model.*;
import com.behsazan.schemaforge.comparison.rule.ComparisonRule;
import com.behsazan.schemaforge.comparison.signature.ForeignKeySignatureFactory;
import com.behsazan.schemaforge.domain.model.ForeignKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class ForeignKeyComparisonRule implements ComparisonRule {
    private final ForeignKeySignatureFactory signatures;
    public ForeignKeyComparisonRule(ForeignKeySignatureFactory signatures) { this.signatures = signatures; }
    @Override public int order() { return 500; }

    @Override
    public List<ComparisonDifference> compare(ComparisonContext context) {
        Map<String, ForeignKey> expectedBySignature = bySignature(context.documentTable().foreignKeys());
        Map<String, ForeignKey> actualBySignature = bySignature(context.databaseTable().foreignKeys());
        Map<String, ForeignKey> expectedByIdentity = byIdentity(context.documentTable().foreignKeys());
        Map<String, ForeignKey> actualByIdentity = byIdentity(context.databaseTable().foreignKeys());
        Set<String> handled = new HashSet<>();
        List<ComparisonDifference> result = new ArrayList<>();

        expectedByIdentity.forEach((identity, expected) -> {
            String expectedSignature = signatures.create(expected);
            if (actualBySignature.containsKey(expectedSignature)) {
                handled.add(expectedSignature);
                return;
            }
            ForeignKey actual = actualByIdentity.get(identity);
            if (actual != null) {
                handled.add(signatures.create(actual));
                result.add(create(DifferenceType.FOREIGN_KEY_CHANGED, DifferenceSeverity.CRITICAL,
                        ResolutionStrategy.DROP_AND_CREATE, name(expected), expectedSignature, signatures.create(actual),
                        "Foreign key target, referenced columns, or referential actions differ"));
            } else {
                result.add(create(DifferenceType.FOREIGN_KEY_MISSING, DifferenceSeverity.CRITICAL,
                        ResolutionStrategy.AUTO_FIX, name(expected), expectedSignature, "MISSING",
                        "Expected foreign key does not exist in database"));
            }
        });

        actualBySignature.forEach((signature, actual) -> {
            if (!expectedBySignature.containsKey(signature) && !handled.contains(signature)
                    && !expectedByIdentity.containsKey(signatures.identity(actual))) {
                result.add(create(DifferenceType.FOREIGN_KEY_EXTRA, DifferenceSeverity.HIGH,
                        ResolutionStrategy.MANUAL_REVIEW, name(actual), "MISSING", signature,
                        "Database contains a foreign key not defined in document"));
            }
        });
        return List.copyOf(result);
    }

    private Map<String, ForeignKey> bySignature(List<ForeignKey> values) {
        return values.stream().collect(Collectors.toMap(signatures::create, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
    }
    private Map<String, ForeignKey> byIdentity(List<ForeignKey> values) {
        return values.stream().collect(Collectors.toMap(signatures::identity, Function.identity(), (first, ignored) -> first, LinkedHashMap::new));
    }
    private static String name(ForeignKey value) { return value.name() == null ? "FOREIGN_KEY" : value.name().value(); }
    private static ComparisonDifference create(DifferenceType type, DifferenceSeverity severity,
                                               ResolutionStrategy resolution, String name, String expected,
                                               String actual, String message) {
        return difference().scope(DifferenceScope.FOREIGN_KEY).type(type).severity(severity).resolution(resolution)
                .objectName(name).property("STRUCTURE").expected(expected).actual(actual).message(message).build();
    }
}
