package com.behsazan.schemaforge.generation.spi;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public record GeneratedArtifact(
        String fileName,
        ArtifactType type,
        byte[] content,
        int order,
        String mediaType) {
    public GeneratedArtifact {
        Objects.requireNonNull(fileName, "fileName must not be null");
        Objects.requireNonNull(type, "type must not be null");
        content = Arrays.copyOf(Objects.requireNonNull(content, "content must not be null"), content.length);
    }

    public static GeneratedArtifact text(String fileName, ArtifactType type, String content, int order, String mediaType) {
        return new GeneratedArtifact(fileName, type, content.getBytes(StandardCharsets.UTF_8), order, mediaType);
    }

    @Override
    public byte[] content() {
        return Arrays.copyOf(content, content.length);
    }
}
