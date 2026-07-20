package com.behsazan.schemaforge.application;

import java.util.Arrays;
import java.util.Objects;

public record GeneratedZip(String fileName, byte[] content) {
    public GeneratedZip {
        Objects.requireNonNull(fileName, "fileName must not be null");
        content = Arrays.copyOf(Objects.requireNonNull(content, "content must not be null"), content.length);
    }

    @Override
    public byte[] content() {
        return Arrays.copyOf(content, content.length);
    }
}
