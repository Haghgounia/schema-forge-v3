package com.behsazan.schemaforge.generation.model;

import java.util.Comparator;
import java.util.List;

public record SqlDocument(List<SqlSection> sections) {
    public SqlDocument {
        sections = sections == null ? List.of() : sections.stream()
                .sorted(Comparator.comparingInt(SqlSection::order))
                .toList();
    }
}
