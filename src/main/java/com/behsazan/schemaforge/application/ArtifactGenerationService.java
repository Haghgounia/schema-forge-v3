package com.behsazan.schemaforge.application;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.generation.artifact.ArtifactBundle;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.generation.core.DdlGenerationEngine;
import com.behsazan.schemaforge.generation.core.DdlGenerationRequest;
import com.behsazan.schemaforge.generation.ddl.model.ScriptOptions;
import com.behsazan.schemaforge.generation.spi.ArtifactType;
import com.behsazan.schemaforge.generation.spi.GeneratedArtifact;
import com.behsazan.schemaforge.packaging.ZipArtifactPackager;
import com.behsazan.schemaforge.reporting.SchemaExcelWriter;
import com.behsazan.schemaforge.specification.adapter.docx.DocxSpecificationParser;
import com.behsazan.schemaforge.specification.spi.SpecificationSource;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Produces Oracle SQL and Excel files and packages all results in one ZIP. */
@Service
public final class ArtifactGenerationService {
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final DocxSpecificationParser parser;
    private final SchemaExcelWriter excelWriter;
    private final DdlGenerationEngine ddlGenerationEngine;
    private final ZipArtifactPackager packager;
    private final Clock clock;

    @Autowired
    public ArtifactGenerationService(
            DocxSpecificationParser parser,
            SchemaExcelWriter excelWriter,
            DdlGenerationEngine ddlGenerationEngine) {
        this(parser, excelWriter, ddlGenerationEngine,
                new ZipArtifactPackager(), Clock.systemDefaultZone());
    }

    ArtifactGenerationService(
            DocxSpecificationParser parser,
            SchemaExcelWriter excelWriter,
            DdlGenerationEngine ddlGenerationEngine,
            ZipArtifactPackager packager,
            Clock clock) {
        this.parser = parser;
        this.excelWriter = excelWriter;
        this.ddlGenerationEngine = ddlGenerationEngine;
        this.packager = packager;
        this.clock = clock;
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

    private List<GeneratedArtifact> artifacts(DatabaseSchema schema, String baseName) {
        var ddlResult = ddlGenerationEngine.generate(DdlGenerationRequest.of(
                schema, DatabaseProduct.ORACLE, ScriptOptions.defaults(), clock));
        if (ddlResult.renderedDdl().isEmpty()) {
            throw new IllegalStateException("Oracle SQL generation produced an empty script for " + baseName);
        }
        byte[] sql = ddlResult.ddl().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] excel = excelWriter.write(schema);
        return List.of(
                new GeneratedArtifact(baseName + ".sql", ArtifactType.SQL, sql, 100, "application/sql; charset=UTF-8"),
                new GeneratedArtifact(baseName + ".xlsx", ArtifactType.EXCEL, excel, 200,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
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

    private String safeName(String value) {
        String safe = value.trim().replaceAll("[^A-Za-z0-9_$#.-]", "_");
        if (safe.isBlank()) {
            throw new IllegalArgumentException("Table name cannot be used as an output file name");
        }
        return safe;
    }
}
