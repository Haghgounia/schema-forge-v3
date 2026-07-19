package com.behsazan.schemaforge.specification.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.specification.spi.SpecificationParser;
import com.behsazan.schemaforge.specification.spi.SpecificationSource;
import java.util.List;
import org.junit.jupiter.api.Test;

class SpecificationParserRegistryTest {
    private final SpecificationParser docxParser = new SpecificationParser() {
        @Override public boolean supports(String fileName) { return fileName != null && fileName.toLowerCase().endsWith(".docx"); }
        @Override public DatabaseSchema parse(SpecificationSource source) { return DatabaseSchema.builder("BIM").build(); }
    };

    @Test void findsParserByFileName() {
        SpecificationParserRegistry registry = new SpecificationParserRegistry(List.of(docxParser));
        assertThat(registry.requireFor("MCB.BIM.TABLE.docx")).isSameAs(docxParser);
    }

    @Test void rejectsUnsupportedFile() {
        SpecificationParserRegistry registry = new SpecificationParserRegistry(List.of(docxParser));
        assertThatThrownBy(() -> registry.requireFor("schema.pdf"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported specification file");
    }
}
