package com.behsazan.schemaforge.specification.adapter.docx;

import com.behsazan.schemaforge.database.service.DatabaseDictionaryCache;
import com.behsazan.schemaforge.dialect.postgresql.PostgreSqlDialect;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.generation.ddl.generator.script.TableScriptGenerator;
import com.behsazan.schemaforge.generation.enrichment.AuditColumnSchemaEnricher;
import com.behsazan.schemaforge.generation.ddl.generator.table.TableDdlGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.table.postgresql.PostgreSqlColumnDefinitionGenerator;
import com.behsazan.schemaforge.generation.ddl.model.RenderContext;
import com.behsazan.schemaforge.generation.ddl.model.ScriptOptions;
import com.behsazan.schemaforge.generation.ddl.renderer.postgresql.PostgreSqlDdlRenderer;
import com.behsazan.schemaforge.generation.oracle.OracleDdlGenerator;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import com.behsazan.schemaforge.generation.spi.GenerationContext;
import com.behsazan.schemaforge.generation.spi.GenerationOptions;
import com.behsazan.schemaforge.specification.spi.SpecificationSource;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Manual batch integration test for generating Oracle and PostgreSQL DDL
 * scripts from all DOCX files in an external directory.
 *
 * <p>Configuration is read from:</p>
 *
 * <pre>
 * schemaforge.ddl
 * </pre>
 *
 * <p>The test is skipped when {@code schemaforge.ddl.enabled=false}.</p>
 */
@SpringBootTest
@EnableConfigurationProperties(
        DocxDirectoryDdlGenerationTest.DdlGenerationProperties.class)
class DocxDirectoryDdlGenerationTest {
    private static final DateTimeFormatter OUTPUT_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final DdlGenerationProperties properties;
    private final DatabaseDictionaryCache databaseDictionaryCache;

    @Autowired
    DocxDirectoryDdlGenerationTest(
            DdlGenerationProperties properties,
            ObjectProvider<DatabaseDictionaryCache> databaseDictionaryCacheProvider) {
        this.properties = properties;
        this.databaseDictionaryCache = databaseDictionaryCacheProvider.getIfAvailable();
    }

    @Test
    void generateOracleAndPostgreSqlScriptsForDirectory() throws Exception {
        Assumptions.assumeTrue(
                properties.enabled(),
                "DOCX directory DDL generation is disabled. "
                        + "Set schemaforge.ddl.enabled=true to run it.");

        validateConfiguration();

        Path inputDirectory = properties.normalizedInputDirectory();
        Path outputDirectory = properties.normalizedOutputDirectory();

        List<Path> docxFiles = findDocxFiles(inputDirectory);

        assertThat(docxFiles)
                .as("DOCX files under %s", inputDirectory)
                .isNotEmpty();

        Path oracleDirectory = outputDirectory.resolve("oracle");
        Path postgreSqlDirectory = outputDirectory.resolve("postgresql");

        if (properties.dialects().oracle()) {
            Files.createDirectories(oracleDirectory);
        }

        if (properties.dialects().postgresql()) {
            Files.createDirectories(postgreSqlDirectory);
        }

        DocxSpecificationParser parser = new DocxSpecificationParser();
        AuditColumnSchemaEnricher auditColumnEnricher = new AuditColumnSchemaEnricher();

        OracleDdlGenerator oracleGenerator =
                properties.dialects().oracle()
                        ? new OracleDdlGenerator(databaseDictionaryCache)
                        : null;

        PostgreSqlComponents postgreSql =
                properties.dialects().postgresql()
                        ? createPostgreSqlComponents()
                        : null;

        List<FileResult> results = new ArrayList<>();
        String outputTimestamp = LocalDateTime.now().format(OUTPUT_TIMESTAMP);

        for (Path docxFile : docxFiles) {
            Path relativeDocx = inputDirectory.relativize(docxFile);

            try (InputStream input = Files.newInputStream(docxFile)) {
                DatabaseSchema schema = auditColumnEnricher.enrich(parser.parse(
                        new SpecificationSource(
                                relativeDocx.toString(),
                                input)));

                Path relativeSql =
                        appendTimestamp(relativeDocx, outputTimestamp, ".sql");

                if (properties.dialects().oracle()) {
                    String oracleSql = generateOracle(
                            schema,
                            relativeDocx,
                            oracleGenerator);

                    writeSql(
                            oracleDirectory.resolve(relativeSql),
                            oracleSql);
                }

                if (properties.dialects().postgresql()) {
                    String postgreSqlScript = generatePostgreSql(
                            schema,
                            postgreSql.generator(),
                            postgreSql.renderer(),
                            postgreSql.dialect(),
                            postgreSql.renderContext());

                    writeSql(
                            postgreSqlDirectory.resolve(relativeSql),
                            postgreSqlScript);
                }

                results.add(new FileResult(
                        relativeDocx.toString(),
                        "OK",
                        ""));
            } catch (Exception exception) {
                results.add(new FileResult(
                        relativeDocx.toString(),
                        "ERROR",
                        rootMessage(exception)));

                if (!properties.options().continueOnError()) {
                    writeReports(outputDirectory, results);
                    throw exception;
                }
            }
        }

        writeReports(outputDirectory, results);

        long succeeded = results.stream()
                .filter(FileResult::succeeded)
                .count();

        long failed = results.size() - succeeded;

        if (properties.report().summary()) {
            printSummary(
                    inputDirectory,
                    outputDirectory,
                    results.size(),
                    succeeded,
                    failed);
        }

        if (properties.options().strict()) {
            assertThat(failed)
                    .as(
                            "DDL generation failures. See %s",
                            outputDirectory
                                    .resolve("generation-results.csv")
                                    .toAbsolutePath())
                    .isZero();
        }
    }

    private void validateConfiguration() {
        assertThat(properties.inputDirectory())
                .as("schemaforge.ddl.input-directory")
                .isNotNull();

        assertThat(properties.outputDirectory())
                .as("schemaforge.ddl.output-directory")
                .isNotNull();

        Path inputDirectory = properties.normalizedInputDirectory();
        Path outputDirectory = properties.normalizedOutputDirectory();

        assertThat(inputDirectory.isAbsolute())
                .as(
                        "schemaforge.ddl.input-directory must be absolute: %s",
                        inputDirectory)
                .isTrue();

        assertThat(outputDirectory.isAbsolute())
                .as(
                        "schemaforge.ddl.output-directory must be absolute: %s",
                        outputDirectory)
                .isTrue();

        assertExternalDirectory(
                inputDirectory,
                "schemaforge.ddl.input-directory");

        assertExternalDirectory(
                outputDirectory,
                "schemaforge.ddl.output-directory");

        assertThat(inputDirectory)
                .as("DOCX input directory")
                .isDirectory();

        assertThat(outputDirectory)
                .as("Output directory must differ from input directory")
                .isNotEqualTo(inputDirectory);

        assertThat(outputDirectory.startsWith(inputDirectory))
                .as(
                        "Output directory must not be inside input directory: %s",
                        outputDirectory)
                .isFalse();

        assertThat(
                properties.dialects().oracle()
                        || properties.dialects().postgresql())
                .as("At least one DDL dialect must be enabled")
                .isTrue();
    }

    private List<Path> findDocxFiles(
            Path inputDirectory) throws Exception {

        if (properties.options().recursive()) {
            try (var paths = Files.walk(inputDirectory)) {
                return paths
                        .filter(Files::isRegularFile)
                        .filter(this::isDocx)
                        .sorted(Comparator.comparing(
                                path -> inputDirectory
                                        .relativize(path)
                                        .toString()))
                        .toList();
            }
        }

        try (var paths = Files.list(inputDirectory)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(this::isDocx)
                    .sorted(Comparator.comparing(
                            path -> inputDirectory
                                    .relativize(path)
                                    .toString()))
                    .toList();
        }
    }

    private PostgreSqlComponents createPostgreSqlComponents() {
        PostgreSqlDialect dialect = new PostgreSqlDialect();

        TableScriptGenerator generator =
                new TableScriptGenerator(
                        new TableDdlGenerator(
                                new PostgreSqlColumnDefinitionGenerator()));

        PostgreSqlDdlRenderer renderer =
                new PostgreSqlDdlRenderer();

        RenderContext renderContext =
                new RenderContext(
                        dialect,
                        new ScriptOptions(
                                true,
                                false,
                                true,
                                true,
                                System.lineSeparator()),
                        Clock.systemUTC(),
                        Map.of());

        return new PostgreSqlComponents(
                dialect,
                generator,
                renderer,
                renderContext);
    }

    private String generateOracle(
            DatabaseSchema schema,
            Path source,
            OracleDdlGenerator generator) {

        var result = generator.generate(
                new GenerationContext(
                        schema,
                        DatabaseType.ORACLE,
                        GenerationOptions.defaults(),
                        Clock.systemUTC()));

        if (result.hasErrors() || result.artifacts().isEmpty()) {
            String messages = result.messages().stream()
                    .map(message ->
                            message.code()
                                    + ": "
                                    + message.message())
                    .reduce((left, right) ->
                            left + " | " + right)
                    .orElse(
                            "Oracle generator returned no artifact");

            throw new IllegalStateException(
                    "Oracle generation failed for "
                            + source
                            + ": "
                            + messages);
        }

        return new String(
                result.artifacts()
                        .getFirst()
                        .content(),
                StandardCharsets.UTF_8);
    }

    private String generatePostgreSql(
            DatabaseSchema schema,
            TableScriptGenerator generator,
            PostgreSqlDdlRenderer renderer,
            PostgreSqlDialect dialect,
            RenderContext renderContext) {

        if (schema.tables().isEmpty()) {
            throw new IllegalStateException(
                    "Parsed specification contains no tables");
        }

        StringBuilder sql = new StringBuilder();
        boolean firstTable = true;

        for (var table : schema.tables()) {
            var script = generator.generate(table, dialect);

            String rendered =
                    renderer.render(
                                    script,
                                    renderContext)
                            .content();

            if (!firstTable) {
                sql.append(System.lineSeparator())
                        .append(System.lineSeparator());

                rendered = removePostgreSqlPreamble(rendered);
            }

            sql.append(rendered.stripTrailing());
            firstTable = false;
        }

        return sql.append(System.lineSeparator())
                .toString();
    }

    private String removePostgreSqlPreamble(
            String sql) {

        String normalized =
                sql.replace("\r\n", "\n");

        String preamble =
                "\\set ON_ERROR_STOP on\n\n";

        return normalized.startsWith(preamble)
                ? normalized.substring(preamble.length())
                : normalized;
    }

    private void writeSql(
            Path outputFile,
            String sql) throws Exception {

        Files.createDirectories(outputFile.getParent());

        if (Files.exists(outputFile)
                && !properties.options().overwrite()) {
            throw new IllegalStateException(
                    "Output file already exists and overwrite is disabled: "
                            + outputFile);
        }

        Files.writeString(
                outputFile,
                sql,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void writeReports(
            Path outputDirectory,
            List<FileResult> results) throws Exception {

        if (properties.report().csv()) {
            writeCsvReport(
                    outputDirectory.resolve(
                            "generation-results.csv"),
                    results);
        }

        if (properties.report().summary()) {
            writeSummaryReport(
                    outputDirectory.resolve(
                            "generation-summary.txt"),
                    results);
        }
    }

    private void writeCsvReport(
            Path report,
            List<FileResult> results) throws Exception {

        Files.createDirectories(report.getParent());

        try (BufferedWriter writer =
                     Files.newBufferedWriter(
                             report,
                             StandardCharsets.UTF_8,
                             StandardOpenOption.CREATE,
                             StandardOpenOption.TRUNCATE_EXISTING)) {

            writer.write("file,status,message");
            writer.newLine();

            for (FileResult result : results) {
                writer.write(csv(result.file()));
                writer.write(',');
                writer.write(csv(result.status()));
                writer.write(',');
                writer.write(csv(result.message()));
                writer.newLine();
            }
        }
    }

    private void writeSummaryReport(
            Path report,
            List<FileResult> results) throws Exception {

        long succeeded = results.stream()
                .filter(FileResult::succeeded)
                .count();

        long failed = results.size() - succeeded;

        String summary = """
                DOCX DDL generation summary
                ===========================

                Files: %d
                Succeeded: %d
                Failed: %d
                Oracle enabled: %s
                PostgreSQL enabled: %s
                Strict mode: %s
                Continue on error: %s
                """.formatted(
                results.size(),
                succeeded,
                failed,
                properties.dialects().oracle(),
                properties.dialects().postgresql(),
                properties.options().strict(),
                properties.options().continueOnError());

        Files.createDirectories(report.getParent());

        Files.writeString(
                report,
                summary,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void printSummary(
            Path inputDirectory,
            Path outputDirectory,
            int files,
            long succeeded,
            long failed) {

        System.out.printf(
                """
                DOCX DDL generation completed:
                  input=%s
                  output=%s
                  files=%d
                  succeeded=%d
                  failed=%d
                %n""",
                inputDirectory,
                outputDirectory,
                files,
                succeeded,
                failed);
    }

    private void assertExternalDirectory(
            Path path,
            String propertyName) {

        Path projectDirectory =
                Path.of("")
                        .toAbsolutePath()
                        .normalize();

        assertThat(path.startsWith(projectDirectory))
                .as(
                        "Property '%s' must point outside "
                                + "the project directory %s, but was %s",
                        propertyName,
                        projectDirectory,
                        path)
                .isFalse();
    }

    private boolean isDocx(
            Path path) {

        return path.getFileName()
                .toString()
                .toLowerCase(Locale.ROOT)
                .endsWith(".docx");
    }

    private Path replaceExtension(
            Path path,
            String extension) {

        String fileName =
                path.getFileName().toString();

        int dot = fileName.lastIndexOf('.');

        String baseName =
                dot < 0
                        ? fileName
                        : fileName.substring(0, dot);

        Path parent = path.getParent();

        return parent == null
                ? Path.of(baseName + extension)
                : parent.resolve(baseName + extension);
    }

    private String rootMessage(
            Throwable throwable) {

        Throwable current = throwable;

        while (current.getCause() != null) {
            current = current.getCause();
        }

        String message = current.getMessage();

        return current.getClass().getSimpleName()
                + (message == null || message.isBlank()
                ? ""
                : ": " + message);
    }

    private String csv(
            String value) {

        String safe =
                value == null ? "" : value;

        return '"'
                + safe.replace("\"", "\"\"")
                + '"';
    }

    @ConfigurationProperties(prefix = "schemaforge.ddl")
    public record DdlGenerationProperties(
            boolean enabled,
            Path inputDirectory,
            Path outputDirectory,
            Dialects dialects,
            Options options,
            Report report) {

        public DdlGenerationProperties {
            dialects =
                    dialects == null
                            ? new Dialects(true, true)
                            : dialects;

            options =
                    options == null
                            ? new Options(
                            true,
                            false,
                            true,
                            true)
                            : options;

            report =
                    report == null
                            ? new Report(true, true)
                            : report;
        }

        Path normalizedInputDirectory() {
            return inputDirectory
                    .toAbsolutePath()
                    .normalize();
        }

        Path normalizedOutputDirectory() {
            return outputDirectory
                    .toAbsolutePath()
                    .normalize();
        }
    }

    public record Dialects(
            boolean oracle,
            boolean postgresql) {
    }

    public record Options(
            boolean recursive,
            boolean strict,
            boolean overwrite,
            boolean continueOnError) {
    }

    public record Report(
            boolean csv,
            boolean summary) {
    }

    private record PostgreSqlComponents(
            PostgreSqlDialect dialect,
            TableScriptGenerator generator,
            PostgreSqlDdlRenderer renderer,
            RenderContext renderContext) {
    }

    private record FileResult(
            String file,
            String status,
            String message) {

        boolean succeeded() {
            return "OK".equals(status);
        }
    }
    private static Path appendTimestamp(Path source, String timestamp, String extension) {
        Path fileName = source.getFileName();
        String value = fileName.toString();
        int dot = value.lastIndexOf('.');
        String stem = dot > 0 ? value.substring(0, dot) : value;
        String timestamped = stem + "-" + timestamp + extension;
        Path parent = source.getParent();
        return parent == null ? Path.of(timestamped) : parent.resolve(timestamped);
    }

}