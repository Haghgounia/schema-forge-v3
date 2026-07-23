package com.behsazan.schemaforge.application;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.discovery.core.DiscoveryEngine;
import com.behsazan.schemaforge.discovery.domain.DiscoveryIssue;
import com.behsazan.schemaforge.discovery.domain.DiscoveryResult;
import com.behsazan.schemaforge.application.database.DatabaseMetadataReader;
import com.behsazan.schemaforge.application.database.DatabaseMetadataReaderRegistry;
import com.behsazan.schemaforge.application.database.GenerationDatabaseProductResolver;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.generation.artifact.ArtifactBundle;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.generation.core.DdlGenerationEngine;
import com.behsazan.schemaforge.generation.core.DdlGenerationRequest;
import com.behsazan.schemaforge.generation.ddl.model.ScriptOptions;
import com.behsazan.schemaforge.generation.spi.ArtifactType;
import com.behsazan.schemaforge.generation.spi.GeneratedArtifact;
import com.behsazan.schemaforge.packaging.ZipArtifactPackager;
import com.behsazan.schemaforge.reporting.CanonicalSchemaCompareExcelWriter;
import com.behsazan.schemaforge.specification.adapter.docx.DocxSpecificationParser;
import com.behsazan.schemaforge.specification.spi.SpecificationSource;
import com.behsazan.schemaforge.specification.domain.ColumnDefinition;
import com.behsazan.schemaforge.specification.domain.DataTypeDefinition;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Produces CREATE SQL for new tables or a comparison Excel for existing tables. */
@Service
public final class ArtifactGenerationService {
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final DocxSpecificationParser parser;
    private final CanonicalSchemaCompareExcelWriter compareExcelWriter;
    private final DatabaseMetadataReaderRegistry metadataReaders;
    private final GenerationDatabaseProductResolver databaseProductResolver;
    private final DdlGenerationEngine ddlGenerationEngine;
    private final ZipArtifactPackager packager;
    private final Clock clock;
    private final DiscoveryEngine discoveryEngine;

    @Autowired
    public ArtifactGenerationService(
            DocxSpecificationParser parser,
            CanonicalSchemaCompareExcelWriter compareExcelWriter,
            DdlGenerationEngine ddlGenerationEngine,
            DatabaseMetadataReaderRegistry metadataReaders,
            GenerationDatabaseProductResolver databaseProductResolver,
            Optional<DiscoveryEngine> discoveryEngine) {
        this(parser, compareExcelWriter, ddlGenerationEngine, metadataReaders,
                databaseProductResolver, new ZipArtifactPackager(), Clock.systemDefaultZone(),
                discoveryEngine.orElse(null));
    }

    ArtifactGenerationService(
            DocxSpecificationParser parser,
            DdlGenerationEngine ddlGenerationEngine,
            ZipArtifactPackager packager,
            Clock clock) {
        this(parser, new CanonicalSchemaCompareExcelWriter(), ddlGenerationEngine,
                new DatabaseMetadataReaderRegistry(List.of()), null, packager, clock, null);
    }

    ArtifactGenerationService(
            DocxSpecificationParser parser,
            CanonicalSchemaCompareExcelWriter compareExcelWriter,
            DdlGenerationEngine ddlGenerationEngine,
            DatabaseMetadataReaderRegistry metadataReaders,
            GenerationDatabaseProductResolver databaseProductResolver,
            ZipArtifactPackager packager,
            Clock clock) {
        this(parser, compareExcelWriter, ddlGenerationEngine, metadataReaders,
                databaseProductResolver, packager, clock, null);
    }

    ArtifactGenerationService(
            DocxSpecificationParser parser,
            CanonicalSchemaCompareExcelWriter compareExcelWriter,
            DdlGenerationEngine ddlGenerationEngine,
            DatabaseMetadataReaderRegistry metadataReaders,
            GenerationDatabaseProductResolver databaseProductResolver,
            ZipArtifactPackager packager,
            Clock clock,
            DiscoveryEngine discoveryEngine) {
        this.parser = parser;
        this.compareExcelWriter = compareExcelWriter;
        this.ddlGenerationEngine = ddlGenerationEngine;
        this.metadataReaders = metadataReaders == null
                ? new DatabaseMetadataReaderRegistry(List.of()) : metadataReaders;
        this.databaseProductResolver = databaseProductResolver;
        this.packager = packager;
        this.clock = clock;
        this.discoveryEngine = discoveryEngine;
    }

    public GeneratedZip generateFromWord(String fileName, InputStream content) {
        String timestamp = timestamp();
        DatabaseSchema schema = parser.parse(new SpecificationSource(fileName, content));
        Table table = requireSingleTable(schema, fileName);
        String baseName = safeName(table.qualifiedName().name().value()) + "-" + timestamp;
        List<GeneratedArtifact> artifacts = artifacts(schema, baseName);
        byte[] zip = packager.packageArtifacts(new ArtifactBundle(baseName, artifacts));
        return new GeneratedZip(baseName + ".zip", zip);
    }

    public GeneratedZip generateFromZip(String fileName, InputStream content) {
        String timestamp = timestamp();
        List<GeneratedArtifact> artifacts = new ArrayList<>();
        Set<String> names = new HashSet<>();
        int documentCount = 0;

        try (ZipInputStream zip = new ZipInputStream(content)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.isDirectory() || !entry.getName().toLowerCase(Locale.ROOT).endsWith(".docx")) {
                    zip.closeEntry();
                    continue;
                }
                byte[] docx = readAll(zip);
                DatabaseSchema schema = parser.parse(new SpecificationSource(entry.getName(), new ByteArrayInputStream(docx)));
                Table table = requireSingleTable(schema, entry.getName());
                String baseName = safeName(table.qualifiedName().name().value()) + "-" + timestamp;
                for (GeneratedArtifact artifact : artifacts(schema, baseName)) {
                    if (!names.add(artifact.fileName())) {
                        throw new IllegalArgumentException("Duplicate table output name in ZIP: " + artifact.fileName());
                    }
                    artifacts.add(artifact);
                }
                documentCount++;
                zip.closeEntry();
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read input ZIP: " + fileName, exception);
        }

        if (documentCount == 0) {
            throw new IllegalArgumentException("Input ZIP does not contain any DOCX file");
        }

        String baseName = "SchemaForge-" + timestamp;
        byte[] result = packager.packageArtifacts(new ArtifactBundle(baseName, artifacts));
        return new GeneratedZip(baseName + ".zip", result);
    }

    private List<GeneratedArtifact> artifacts(
            DatabaseSchema schema,
            String baseName) {


        List<GeneratedArtifact> artifacts =
                new ArrayList<>();


        Table documentTable =
                schema.tables().getFirst();


        DatabaseProduct targetProduct =
                resolveTargetProduct();



        /*
         * 1- Always generate DDL
         */
        var ddlResult =
                ddlGenerationEngine.generate(
                        DdlGenerationRequest.of(
                                schema,
                                targetProduct,
                                ScriptOptions.defaults(),
                                clock
                        )
                );


        if (ddlResult.renderedDdl().isEmpty()) {
            throw new IllegalStateException(
                    targetProduct
                            + " SQL generation produced an empty script for "
                            + baseName
            );
        }


        String ddl = prependDiscoveryIssues(ddlResult.ddl(), documentTable);

        byte[] sql =
                ddl.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8
                );


        artifacts.add(
                new GeneratedArtifact(
                        baseName + ".sql",
                        ArtifactType.SQL,
                        sql,
                        100,
                        "application/sql; charset=UTF-8"
                )
        );



        /*
         * 2- Generate comparison Excel only when table exists
         */
        DatabaseMetadataReader reader =
                metadataReaders.find(targetProduct)
                        .orElse(null);


        if (reader != null) {


            var databaseTable =
                    reader.readTable(
                            documentTable.qualifiedName()
                                    .schema()
                                    .value(),

                            documentTable.qualifiedName()
                                    .name()
                                    .value()
                    );


            if (databaseTable.isPresent()) {


                byte[] excel =
                        compareExcelWriter.write(
                                documentTable,
                                databaseTable.get(),
                                reader.columnUsageCounts()
                        );


                String excelName =
                        safeName(
                                documentTable.qualifiedName()
                                        .name()
                                        .value()
                        )
                                + "-"
                                + timestamp()
                                + ".xlsx";


                artifacts.add(
                        new GeneratedArtifact(
                                excelName,
                                ArtifactType.EXCEL,
                                excel,
                                100,
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                );
            }
        }


        return List.copyOf(artifacts);
    }


    private String prependDiscoveryIssues(String ddl, Table documentTable) {
        if (discoveryEngine == null) {
            return ddl;
        }

        DiscoveryResult result;
        try {
            result = discoveryEngine.discover(toTableDefinition(documentTable));
        } catch (RuntimeException exception) {
            return ddl;
        }

        List<DiscoveryIssue> actionableIssues = result.issues().stream()
                .filter(issue -> issue.severity() != com.behsazan.schemaforge.discovery.domain.DiscoverySeverity.INFO)
                .toList();
        if (actionableIssues.isEmpty()) {
            return ddl;
        }

        String lineSeparator = System.lineSeparator();
        StringBuilder hints = new StringBuilder();
        hints.append("-- =====================================================").append(lineSeparator);
        hints.append("-- SCHEMAFORGE DISCOVERY WARNINGS").append(lineSeparator);
        hints.append("-- =====================================================").append(lineSeparator);

        for (DiscoveryIssue issue : actionableIssues) {
            hints.append("-- ").append(issue.severity()).append(": ").append(issue.code()).append(lineSeparator);
            if (issue.columnName() != null && !issue.columnName().isBlank()) {
                hints.append("-- Column: ").append(issue.columnName()).append(lineSeparator);
            }
            hints.append("-- ").append(issue.message()).append(lineSeparator);
            String locations = issue.details().get("locations");
            if (locations != null && !locations.isBlank()) {
                hints.append("-- Existing usages: ").append(locations).append(lineSeparator);
            }
            hints.append("-- Action: Review the difference before executing this script.")
                    .append(lineSeparator).append(lineSeparator);
        }
        return hints.append(ddl).toString();
    }

    private TableDefinition toTableDefinition(Table table) {
        List<ColumnDefinition> columns = table.columns().stream()
                .map(column -> new ColumnDefinition(
                        column.name().value(),
                        new DataTypeDefinition(
                                column.dataType().name().value(),
                                column.dataType().length(),
                                column.dataType().precision(),
                                column.dataType().scale()),
                        column.nullable(),
                        column.defaultValue().isPresent() ? column.defaultValue().expression() : null,
                        column.description().value(),
                        table.primaryKey().map(pk -> pk.columns().contains(column.name())).orElse(false),
                        false,
                        false))
                .toList();

        return new TableDefinition(
                table.qualifiedName().schema().value(),
                table.qualifiedName().name().value(),
                table.description().value(),
                columns,
                null,
                List.of(),
                List.of(),
                null,
                java.util.Map.of());
    }

    private DatabaseProduct resolveTargetProduct() {
        return databaseProductResolver == null ? DatabaseProduct.ORACLE : databaseProductResolver.resolve();
    }

    private Table requireSingleTable(DatabaseSchema schema, String sourceName) {
        if (schema.tables().size() != 1) {
            throw new IllegalArgumentException(
                    "Each DOCX must describe exactly one table: " + sourceName + " (found " + schema.tables().size() + ")");
        }
        return schema.tables().getFirst();
    }

    private byte[] readAll(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        input.transferTo(output);
        return output.toByteArray();
    }

    private String timestamp() {
        ZoneId zone = clock.getZone();
        return LocalDateTime.ofInstant(clock.instant(), zone).format(TIMESTAMP);
    }

    private String timestampForComparison() {
        ZoneId zone = clock.getZone();
        return LocalDateTime.ofInstant(clock.instant(), zone)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    private String safeName(String value) {
        String safe = value.trim().replaceAll("[^A-Za-z0-9_$#.-]", "_");
        if (safe.isBlank()) {
            throw new IllegalArgumentException("Table name cannot be used as an output file name");
        }
        return safe;
    }
}
