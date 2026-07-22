package com.behsazan.schemaforge.comparison.rule;

import static com.behsazan.schemaforge.comparison.model.ComparisonDifferenceBuilder.difference;

import com.behsazan.schemaforge.comparison.context.ComparisonContext;
import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.comparison.model.DifferenceScope;
import com.behsazan.schemaforge.comparison.model.DifferenceSeverity;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.comparison.model.ResolutionStrategy;
import com.behsazan.schemaforge.domain.model.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;


@Component
public final class ColumnExistenceComparisonRule implements ComparisonRule {

    private static final Set<String> IGNORE_EXTRA_COLUMNS = Set.of(
            "CREATED_BY",
            "CREATED_DATE",
            "LAST_MODIFIED_BY",
            "LAST_MODIFIED_DATE"
    );


    @Override
    public int order() {
        return 100;
    }


    @Override
    public List<ComparisonDifference> compare(
            ComparisonContext context) {


        Map<String, Column> document =
                context.documentColumns().asMap();


        Map<String, Column> database =
                context.databaseColumns().asMap();


        List<ComparisonDifference> result =
                new ArrayList<>();


        document.forEach((name, column) -> {

            if (!database.containsKey(name)) {

                result.add(
                        difference()
                                .scope(DifferenceScope.COLUMN)
                                .type(DifferenceType.COLUMN_MISSING)
                                .severity(DifferenceSeverity.CRITICAL)
                                .resolution(ResolutionStrategy.AUTO_FIX)
                                .objectName(column.name().value())
                                .property("EXISTENCE")
                                .expected("PRESENT")
                                .actual("MISSING")
                                .message("Expected column does not exist in database")
                                .build()
                );
            }
        });



        database.forEach((name, column) -> {


            if (!document.containsKey(name)) {


                if (isAuditColumn(name)) {
                    return;
                }


                result.add(
                        difference()
                                .scope(DifferenceScope.COLUMN)
                                .type(DifferenceType.COLUMN_EXTRA)
                                .severity(DifferenceSeverity.HIGH)
                                .resolution(ResolutionStrategy.MANUAL_REVIEW)
                                .objectName(column.name().value())
                                .property("EXISTENCE")
                                .expected("MISSING")
                                .actual("PRESENT")
                                .message("Database contains a column not defined in document")
                                .build()
                );
            }
        });


        return List.copyOf(result);
    }



    private boolean isAuditColumn(String columnName) {

        return columnName != null
                && IGNORE_EXTRA_COLUMNS.contains(
                columnName.toUpperCase()
        );
    }
}