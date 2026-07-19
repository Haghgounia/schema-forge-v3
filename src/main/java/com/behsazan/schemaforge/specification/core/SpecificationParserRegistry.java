package com.behsazan.schemaforge.specification.core;

import com.behsazan.schemaforge.specification.spi.SpecificationParser;
import java.util.List;
import java.util.Objects;

public final class SpecificationParserRegistry {
    private final List<SpecificationParser> parsers;

    public SpecificationParserRegistry(List<SpecificationParser> parsers) {
        this.parsers = List.copyOf(Objects.requireNonNull(parsers, "parsers must not be null"));
    }

    public SpecificationParser requireFor(String fileName) {
        Objects.requireNonNull(fileName, "fileName must not be null");
        return parsers.stream()
                .filter(parser -> parser.supports(fileName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported specification file: " + fileName));
    }

    public List<SpecificationParser> all() {
        return parsers;
    }
}
