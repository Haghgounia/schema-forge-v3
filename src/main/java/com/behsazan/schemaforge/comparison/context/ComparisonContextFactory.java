package com.behsazan.schemaforge.comparison.context;

import com.behsazan.schemaforge.domain.model.Table;
import org.springframework.stereotype.Component;

@Component
public final class ComparisonContextFactory {
    public ComparisonContext create(Table documentTable, Table databaseTable) {
        return new ComparisonContext(documentTable, databaseTable);
    }
}
