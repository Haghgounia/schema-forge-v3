package com.behsazan.schemaforge.comparison.context;

import com.behsazan.schemaforge.domain.model.Table;
import java.util.Objects;

public final class ComparisonContext {
    private final Table documentTable;
    private final Table databaseTable;
    private final ColumnLookup documentColumns;
    private final ColumnLookup databaseColumns;

    ComparisonContext(Table documentTable, Table databaseTable) {
        this.documentTable = Objects.requireNonNull(documentTable, "documentTable must not be null");
        this.databaseTable = Objects.requireNonNull(databaseTable, "databaseTable must not be null");
        this.documentColumns = ColumnLookup.of(documentTable.columns());
        this.databaseColumns = ColumnLookup.of(databaseTable.columns());
    }

    public Table documentTable() { return documentTable; }
    public Table databaseTable() { return databaseTable; }
    public ColumnLookup documentColumns() { return documentColumns; }
    public ColumnLookup databaseColumns() { return databaseColumns; }
}
