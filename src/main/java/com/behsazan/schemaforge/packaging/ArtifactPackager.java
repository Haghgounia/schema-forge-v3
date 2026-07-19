package com.behsazan.schemaforge.packaging;

import com.behsazan.schemaforge.generation.artifact.ArtifactBundle;

public interface ArtifactPackager {
    byte[] packageArtifacts(ArtifactBundle bundle);
}
