package com.behsazan.schemaforge.generation.ddl.model;

import java.util.Comparator;
import java.util.List;

public record DdlScript(List<DdlSection> sections) {
    public DdlScript {
        sections = sections == null ? List.of() : sections.stream()
                .sorted(Comparator.comparingInt(section -> section.phase().order()))
                .toList();
    }

    public List<DdlStatement> statements() {
        return sections.stream().flatMap(section -> section.statements().stream()).toList();
    }

    public boolean isEmpty() {
        return sections.stream().allMatch(DdlSection::isEmpty);
    }
}
