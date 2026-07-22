package com.behsazan.schemaforge.comparison.context;

import com.behsazan.schemaforge.domain.model.Column;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ColumnLookup {
    private final Map<String, Column> byName;

    private ColumnLookup(List<Column> columns) {
        Map<String, Column> values = new LinkedHashMap<>();
        for (Column column : columns) {
            Column previous = values.put(column.name().normalized(), column);
            if (previous != null) throw new IllegalArgumentException("duplicate column: " + column.name());
        }
        byName = Collections.unmodifiableMap(values);
    }

    public static ColumnLookup of(List<Column> columns) { return new ColumnLookup(List.copyOf(columns)); }
    public Optional<Column> find(String normalizedName) { return Optional.ofNullable(byName.get(normalizedName)); }
    public Map<String, Column> asMap() { return byName; }
    public int size() { return byName.size(); }
}
