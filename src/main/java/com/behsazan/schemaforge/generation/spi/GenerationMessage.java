package com.behsazan.schemaforge.generation.spi;

public record GenerationMessage(
        GenerationSeverity severity,
        String code,
        String message,
        String objectName) {
}
