package com.behsazan.schemaforge.specification.adapter.docx;

import com.behsazan.schemaforge.generation.oracle.OracleDdlGenerator;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import com.behsazan.schemaforge.generation.spi.GeneratedArtifact;
import com.behsazan.schemaforge.generation.spi.GenerationContext;
import com.behsazan.schemaforge.generation.spi.GenerationMessage;
import com.behsazan.schemaforge.generation.spi.GenerationOptions;
import com.behsazan.schemaforge.specification.spi.SpecificationSource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("Temporarily disabled until DOCX parser is fixed")
class DocxCorpusBatchTest {
    private static final String INPUT_DIRECTORY_PROPERTY = "schemaforge.corpus.dir";
    private static final String OUTPUT_DIRECTORY_PROPERTY = "schemaforge.corpus.output";
    private static final String STRICT_PROPERTY = "schemaforge.corpus.strict";
    private static final Pattern UNSUPPORTED_DATA_TYPE = Pattern.compile(
            "Unsupported data type:\\s*(.+)", Pattern.CASE_INSENSITIVE);

    @Test
    void scanCompleteDocxCorpusAndWriteReports() throws Exception {
        Path inputDirectory = Path.of(System.getProperty(INPUT_DIRECTORY_PROPERTY, "samples/docx"));
        Path outputDirectory = Path.of(System.getProperty(
                OUTPUT_DIRECTORY_PROPERTY,
                "target/schemaforge-corpus"));
        boolean strict = Boolean.parseBoolean(System.getProperty(STRICT_PROPERTY, "false"));

        assertThat(inputDirectory)
                .as("DOCX corpus directory. Set it with -D%s=<directory>", INPUT_DIRECTORY_PROPERTY)
                .isDirectory();

        Files.createDirectories(outputDirectory);
        Path sqlDirectory = outputDirectory.resolve("sql");
        Files.createDirectories(sqlDirectory);

        List<Path> docxFiles;
        try (var paths = Files.walk(inputDirectory)) {
            docxFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(this::isDocx)
                    .sorted(Comparator.comparing(path -> inputDirectory.relativize(path).toString()))
                    .toList();
        }

        assertThat(docxFiles)
                .as("DOCX files under %s", inputDirectory.toAbsolutePath())
                .isNotEmpty();

        DocxSpecificationParser parser = new DocxSpecificationParser();
        OracleDdlGenerator generator = new OracleDdlGenerator();
        List<CorpusResult> results = new ArrayList<>();
        Map<String, Integer> unknownDataTypes = new LinkedHashMap<>();

        for (Path docxFile : docxFiles) {
            Path relativeFile = inputDirectory.relativize(docxFile);
            try (InputStream input = Files.newInputStream(docxFile)) {
                var schema = parser.parse(new SpecificationSource(relativeFile.toString(), input));
                var generationResult = generator.generate(new GenerationContext(
                        schema,
                        DatabaseType.ORACLE,
                        GenerationOptions.defaults(),
                        Clock.systemUTC()));

                if (generationResult.hasErrors() || generationResult.artifacts().isEmpty()) {
                    String messages = generationResult.messages().stream()
                            .map(this::formatGenerationMessage)
                            .reduce((left, right) -> left + " | " + right)
                            .orElse("Oracle generator returned no artifact");
                    results.add(new CorpusResult(relativeFile.toString(), "GENERATION_ERROR", messages));
                    continue;
                }

                writeArtifacts(sqlDirectory, relativeFile, generationResult.artifacts());
                String warnings = generationResult.messages().stream()
                        .map(this::formatGenerationMessage)
                        .reduce((left, right) -> left + " | " + right)
                        .orElse("");
                results.add(new CorpusResult(
                        relativeFile.toString(),
                        warnings.isBlank() ? "OK" : "WARNING",
                        warnings));
            } catch (Exception exception) {
                String message = rootMessage(exception);
                Matcher matcher = UNSUPPORTED_DATA_TYPE.matcher(message);
                if (matcher.find()) {
                    String dataType = matcher.group(1).trim().toUpperCase(Locale.ROOT);
                    unknownDataTypes.merge(dataType, 1, Integer::sum);
                }
                results.add(new CorpusResult(relativeFile.toString(), "PARSE_ERROR", message));
            }
        }

        writeCorpusResults(outputDirectory.resolve("corpus-results.csv"), results);
        writeUnknownDataTypes(outputDirectory.resolve("unknown-datatypes.csv"), unknownDataTypes);
        writeSummary(outputDirectory.resolve("summary.txt"), inputDirectory, results, unknownDataTypes);

        long failed = results.stream().filter(CorpusResult::failed).count();
        System.out.printf(
                "SchemaForge corpus scan completed: files=%d, failed=%d, report=%s%n",
                results.size(),
                failed,
                outputDirectory.toAbsolutePath());

        if (strict) {
            assertThat(failed)
                    .as("Corpus failures. See %s", outputDirectory.resolve("corpus-results.csv").toAbsolutePath())
                    .isZero();
        }
    }

    private boolean isDocx(Path path) {
        return path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".docx");
    }

    private void writeArtifacts(Path sqlDirectory, Path relativeDocx, List<GeneratedArtifact> artifacts)
            throws Exception {
        Path relativeParent = relativeDocx.getParent();
        Path targetDirectory = relativeParent == null ? sqlDirectory : sqlDirectory.resolve(relativeParent);
        Files.createDirectories(targetDirectory);

        for (GeneratedArtifact artifact : artifacts) {
            String artifactName = artifact.fileName();
            if (artifacts.size() == 1) {
                artifactName = removeExtension(relativeDocx.getFileName().toString()) + ".sql";
            }
            Files.write(
                    targetDirectory.resolve(artifactName),
                    artifact.content(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    private void writeCorpusResults(Path report, List<CorpusResult> results) throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(report, StandardCharsets.UTF_8)) {
            writer.write("file,status,message");
            writer.newLine();
            for (CorpusResult result : results) {
                writer.write(csv(result.fileName()));
                writer.write(',');
                writer.write(csv(result.status()));
                writer.write(',');
                writer.write(csv(result.message()));
                writer.newLine();
            }
        }
    }

    private void writeUnknownDataTypes(Path report, Map<String, Integer> unknownDataTypes) throws Exception {
        List<Map.Entry<String, Integer>> entries = unknownDataTypes.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .toList();

        try (BufferedWriter writer = Files.newBufferedWriter(report, StandardCharsets.UTF_8)) {
            writer.write("data_type,count");
            writer.newLine();
            for (Map.Entry<String, Integer> entry : entries) {
                writer.write(csv(entry.getKey()));
                writer.write(',');
                writer.write(Integer.toString(entry.getValue()));
                writer.newLine();
            }
        }
    }

    private void writeSummary(
            Path report,
            Path inputDirectory,
            List<CorpusResult> results,
            Map<String, Integer> unknownDataTypes) throws Exception {
        long ok = results.stream().filter(result -> result.status().equals("OK")).count();
        long warnings = results.stream().filter(result -> result.status().equals("WARNING")).count();
        long parseErrors = results.stream().filter(result -> result.status().equals("PARSE_ERROR")).count();
        long generationErrors = results.stream().filter(result -> result.status().equals("GENERATION_ERROR")).count();

        List<String> lines = List.of(
                "Input directory: " + inputDirectory.toAbsolutePath(),
                "Files processed: " + results.size(),
                "Succeeded: " + ok,
                "Warnings: " + warnings,
                "Parse errors: " + parseErrors,
                "Generation errors: " + generationErrors,
                "Unknown data type expressions: " + unknownDataTypes.size());
        Files.write(report, lines, StandardCharsets.UTF_8);
    }

    private String formatGenerationMessage(GenerationMessage message) {
        String objectName = message.objectName() == null ? "" : " [" + message.objectName() + "]";
        return message.severity() + " " + message.code() + objectName + ": " + message.message();
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        String message = current.getMessage();
        return message == null || message.isBlank() ? current.getClass().getName() : message;
    }

    private String csv(String value) {
        String safe = value == null ? "" : value.replace("\r", " ").replace("\n", " ");
        return '"' + safe.replace("\"", "\"\"") + '"';
    }

    private String removeExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot < 0 ? fileName : fileName.substring(0, dot);
    }

    private record CorpusResult(String fileName, String status, String message) {
        private boolean failed() {
            return status.equals("PARSE_ERROR") || status.equals("GENERATION_ERROR");
        }
    }
}
