package com.behsazan.schemaforge.generation.artifact;

import com.behsazan.schemaforge.generation.spi.GeneratedArtifact;
import java.util.List;

public record ArtifactBundle(String baseName, List<GeneratedArtifact> artifacts) {
    public ArtifactBundle {
        artifacts = artifacts == null ? List.of() : List.copyOf(artifacts);
    }
}
