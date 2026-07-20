package com.behsazan.schemaforge.discovery.snapshot;

import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.Table;
import java.util.Objects;

public record ColumnUsage(Table table, Column column) {

    public ColumnUsage {
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(column, "column must not be null");
    }
}
