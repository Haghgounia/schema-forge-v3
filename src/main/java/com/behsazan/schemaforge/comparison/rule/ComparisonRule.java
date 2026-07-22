package com.behsazan.schemaforge.comparison.rule;

import com.behsazan.schemaforge.comparison.context.ComparisonContext;
import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import java.util.List;

public interface ComparisonRule {
    int order();
    List<ComparisonDifference> compare(ComparisonContext context);
}
