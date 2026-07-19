package com.behsazan.schemaforge.specification.application;

import com.behsazan.schemaforge.specification.core.SpecificationParserRegistry;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.specification.spi.SpecificationParser;
import com.behsazan.schemaforge.specification.spi.SpecificationSource;

public final class ParseSpecificationService {
    private final SpecificationParserRegistry parserRegistry;

    public ParseSpecificationService(SpecificationParserRegistry parserRegistry) {
        this.parserRegistry = parserRegistry;
    }

    public DatabaseSchema parse(SpecificationSource source) {
        SpecificationParser parser = parserRegistry.requireFor(source.fileName());
        return parser.parse(source);
    }
}
