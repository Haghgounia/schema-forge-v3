package com.behsazan.schemaforge.specification.adapter.ea;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.specification.spi.SpecificationParser;
import com.behsazan.schemaforge.specification.spi.SpecificationSource;

/** Migration target for the v2 Enterprise Architect XMI parser. */
public final class EaXmiSpecificationParser implements SpecificationParser {
    @Override
    public boolean supports(String fileName) {
        return fileName != null && fileName.toLowerCase().endsWith(".xml");
    }

    @Override
    public DatabaseSchema parse(SpecificationSource source) {
        throw new UnsupportedOperationException("EA XMI parser implementation will be migrated from SchemaForge v2");
    }
}
