package com.behsazan.schemaforge.generation.spi;

import java.util.Comparator;
import java.util.List;

public record GenerationResult(List<GeneratedArtifact> artifacts, List<GenerationMessage> messages) {
    public GenerationResult {
        artifacts = artifacts == null ? List.of() : artifacts.stream()
                .sorted(Comparator.comparingInt(GeneratedArtifact::order))
                .toList();
        messages = messages == null ? List.of() : List.copyOf(messages);
    }

    public boolean hasErrors() {
        return messages.stream().anyMatch(message -> message.severity() == GenerationSeverity.ERROR);
    }
}
