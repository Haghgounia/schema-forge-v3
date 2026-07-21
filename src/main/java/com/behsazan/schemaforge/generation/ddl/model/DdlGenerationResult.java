package com.behsazan.schemaforge.generation.ddl.model;

import java.util.List;

public record DdlGenerationResult(DdlScript script, List<DdlGenerationMessage> messages) {
    public DdlGenerationResult {
        script = script == null ? new DdlScript(List.of()) : script;
        messages = messages == null ? List.of() : List.copyOf(messages);
    }

    public boolean hasErrors() {
        return messages.stream()
                .anyMatch(message -> message.severity() == DdlGenerationSeverity.ERROR);
    }
}
