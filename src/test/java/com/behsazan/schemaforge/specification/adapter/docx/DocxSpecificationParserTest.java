package com.behsazan.schemaforge.specification.adapter.docx;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.specification.spi.SpecificationSource;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class DocxSpecificationParserTest {
    private final DocxSpecificationParser parser = new DocxSpecificationParser();

    @Test
    void parsesCitiesDocumentIntoCanonicalSchema() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/samples/BIM.TBL.CITIES.V1.1.docx")) {
            assertThat(input).isNotNull();
            DatabaseSchema schema = parser.parse(new SpecificationSource("BIM.TBL.CITIES.V1.1.docx", input));

            assertThat(schema.name().value()).isEqualTo("BIM");
            assertThat(schema.tables()).hasSize(1);
            assertThat(schema.sequences()).hasSize(1);

            Table table = schema.tables().getFirst();
            assertThat(table.qualifiedName().name().value()).isEqualTo("CITIES");
            assertThat(table.columns()).hasSize(16);
            assertThat(table.primaryKey()).isPresent();
            assertThat(table.uniqueKeys()).hasSize(2);
            assertThat(table.foreignKeys()).hasSize(1);
            assertThat(table.findColumn("CITY_ID")).isPresent();
            var cityId = table.findColumn("CITY_ID").orElseThrow();
            assertThat(cityId.defaultValue()).isNotNull();
            assertThat(cityId.defaultValue().expression())
                    .isEqualTo("BIM.SEQ_CITIES.NEXTVAL");
        }
    }

    @Test
    void supportsDocxCaseInsensitively() {
        assertThat(parser.supports("TABLE.DOCX")).isTrue();
        assertThat(parser.supports("TABLE.xlsx")).isFalse();
    }
}
