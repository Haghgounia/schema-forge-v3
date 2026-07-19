package com.behsazan.schemaforge.specification.adapter.docx;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.specification.spi.SpecificationParser;
import com.behsazan.schemaforge.specification.spi.SpecificationSource;

/** Migration target for the v2 Apache POI DOCX parser. */
public final class DocxSpecificationParser implements SpecificationParser {
    @Override
    public boolean supports(String fileName) {
        return fileName != null && fileName.toLowerCase().endsWith(".docx");
    }

    @Override
    public DatabaseSchema parse(SpecificationSource source) {
        throw new UnsupportedOperationException("DOCX parser implementation will be migrated from SchemaForge v2");
    }
}
