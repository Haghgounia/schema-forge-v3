package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.generation.spi.DatabaseType;
import com.behsazan.schemaforge.generation.spi.GenerationContext;
import com.behsazan.schemaforge.generation.spi.GenerationOptions;
import com.behsazan.schemaforge.specification.adapter.docx.DocxSpecificationParser;
import com.behsazan.schemaforge.specification.spi.SpecificationSource;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class GenerateOracleScriptsFromSamplesTest {

    @Test
    void generateOracleScriptsFromSampleDocxFiles() throws Exception {
        Path inputDirectory = Path.of("samples", "docx");
        Path outputDirectory = Path.of("samples", "sql");
        Files.createDirectories(outputDirectory);

        DocxSpecificationParser parser = new DocxSpecificationParser();
        OracleDdlGenerator generator = new OracleDdlGenerator();

        try (var paths = Files.list(inputDirectory)) {
            List<Path> docxFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".docx"))
                    .sorted()
                    .toList();

            for (Path docxFile : docxFiles) {
                try (InputStream input = Files.newInputStream(docxFile)) {
                    var schema = parser.parse(new SpecificationSource(docxFile.getFileName().toString(), input));
                    var result = generator.generate(new GenerationContext(
                            schema,
                            DatabaseType.ORACLE,
                            GenerationOptions.defaults(),
                            Clock.systemDefaultZone()));

                    if (result.hasErrors() || result.artifacts().isEmpty()) {
                        throw new IllegalStateException("Oracle script generation failed for " + docxFile);
                    }

                    String fileName = removeExtension(docxFile.getFileName().toString()) + ".sql";
                    Files.write(outputDirectory.resolve(fileName), result.artifacts().getFirst().content());
                }
            }
        }
    }

    private String removeExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }
}
