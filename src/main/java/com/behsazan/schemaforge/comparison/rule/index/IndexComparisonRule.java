package com.behsazan.schemaforge.comparison.rule.index;

import static com.behsazan.schemaforge.comparison.model.ComparisonDifferenceBuilder.difference;

import com.behsazan.schemaforge.comparison.context.ComparisonContext;
import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.comparison.model.DifferenceScope;
import com.behsazan.schemaforge.comparison.model.DifferenceSeverity;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.comparison.model.ResolutionStrategy;
import com.behsazan.schemaforge.comparison.normalizer.IdentifierNormalizer;
import com.behsazan.schemaforge.comparison.rule.ComparisonRule;
import com.behsazan.schemaforge.comparison.signature.IndexSignatureFactory;
import com.behsazan.schemaforge.domain.model.Index;
import com.behsazan.schemaforge.domain.model.PrimaryKey;
import com.behsazan.schemaforge.domain.model.UniqueKey;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;


@Component
public final class IndexComparisonRule implements ComparisonRule {

    private final IndexSignatureFactory signatures;


    public IndexComparisonRule(IndexSignatureFactory signatures) {
        this.signatures = Objects.requireNonNull(signatures);
    }


    @Override
    public int order() {
        return 600;
    }


    @Override
    public List<ComparisonDifference> compare(
            ComparisonContext context) {


        List<Index> expected =
                context.documentTable().indexes();


        List<Index> actual =
                context.databaseTable().indexes();


        Map<String, Index> actualBySignature =
                bySignature(actual);


        Set<Index> matchedActual =
                Collections.newSetFromMap(
                        new IdentityHashMap<>()
                );


        List<ComparisonDifference> result =
                new ArrayList<>();



        for (Index wanted : expected) {


            Index exact =
                    actualBySignature.get(
                            signatures.create(wanted)
                    );


            if (exact != null
                    && !matchedActual.contains(exact)) {

                matchedActual.add(exact);
                continue;
            }


            Index sameName =
                    findByName(
                            actual,
                            wanted
                    );


            if (sameName != null
                    && !matchedActual.contains(sameName)) {


                matchedActual.add(sameName);


                result.add(
                        create(
                                DifferenceType.INDEX_CHANGED,
                                DifferenceSeverity.HIGH,
                                ResolutionStrategy.DROP_AND_CREATE,
                                name(wanted),
                                signatures.create(wanted),
                                signatures.create(sameName),
                                "Index exists but its type, columns, order, or sort direction differs"
                        )
                );


            } else {


                result.add(
                        create(
                                DifferenceType.INDEX_MISSING,
                                DifferenceSeverity.HIGH,
                                ResolutionStrategy.AUTO_FIX,
                                name(wanted),
                                signatures.create(wanted),
                                "MISSING",
                                "Expected index does not exist in database"
                        )
                );
            }
        }



        for (Index existing : actual) {


            if (!matchedActual.contains(existing)) {


                if (isConstraintSupportingIndex(
                        context,
                        existing)) {

                    continue;
                }


                result.add(
                        create(
                                DifferenceType.INDEX_EXTRA,
                                DifferenceSeverity.MEDIUM,
                                ResolutionStrategy.MANUAL_REVIEW,
                                name(existing),
                                "MISSING",
                                signatures.create(existing),
                                "Database contains an index not defined in document"
                        )
                );
            }
        }


        return List.copyOf(result);
    }



    private boolean isConstraintSupportingIndex(
            ComparisonContext context,
            Index index) {


        String indexName =
                IdentifierNormalizer.normalize(
                        index.name()
                );


        Optional<PrimaryKey> primaryKey =
                context.databaseTable()
                        .primaryKey();


        if (primaryKey.isPresent()) {

            String pkName =
                    IdentifierNormalizer.normalize(
                            primaryKey.get()
                                    .name()
                    );


            if (indexName.equals(pkName)) {
                return true;
            }
        }


        return context.databaseTable()
                .uniqueKeys()
                .stream()
                .map(UniqueKey::name)
                .map(IdentifierNormalizer::normalize)
                .anyMatch(indexName::equals);
    }



    private Map<String, Index> bySignature(
            List<Index> indexes) {


        return indexes.stream()
                .collect(
                        Collectors.toMap(
                                signatures::create,
                                Function.identity(),
                                (first, ignored) -> first,
                                LinkedHashMap::new
                        )
                );
    }



    private static Index findByName(
            List<Index> indexes,
            Index wanted) {


        String normalized =
                IdentifierNormalizer.normalize(
                        wanted.name()
                );


        if (normalized.isBlank()) {
            return null;
        }


        return indexes.stream()
                .filter(
                        value ->
                                normalized.equals(
                                        IdentifierNormalizer.normalize(
                                                value.name()
                                        )
                                )
                )
                .findFirst()
                .orElse(null);
    }



    private static String name(Index value) {

        return value.name() == null
                ? "INDEX"
                : value.name().value();
    }



    private static ComparisonDifference create(
            DifferenceType type,
            DifferenceSeverity severity,
            ResolutionStrategy resolution,
            String name,
            String expected,
            String actual,
            String message) {


        return difference()
                .scope(DifferenceScope.INDEX)
                .type(type)
                .severity(severity)
                .resolution(resolution)
                .objectName(name)
                .property("STRUCTURE")
                .expected(expected)
                .actual(actual)
                .message(message)
                .build();
    }
}