package com.behsazan.schemaforge.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.behsazan.schemaforge.dialect.DialectRegistry;
import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.dialect.postgresql.PostgreSqlDialect;
import com.behsazan.schemaforge.generation.core.DdlGenerationEngine;
import com.behsazan.schemaforge.generation.ddl.generator.table.ColumnDefinitionGeneratorRegistry;
import com.behsazan.schemaforge.generation.ddl.generator.table.oracle.OracleColumnDefinitionGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.table.postgresql.PostgreSqlColumnDefinitionGenerator;
import com.behsazan.schemaforge.generation.ddl.renderer.RendererRegistry;
import com.behsazan.schemaforge.generation.ddl.renderer.oracle.OracleDdlRenderer;
import com.behsazan.schemaforge.generation.ddl.renderer.postgresql.PostgreSqlDdlRenderer;
import java.util.List;
import com.behsazan.schemaforge.packaging.ZipArtifactPackager;
import com.behsazan.schemaforge.reporting.CanonicalSchemaCompareExcelWriter;
import com.behsazan.schemaforge.application.database.DatabaseMetadataReader;
import com.behsazan.schemaforge.application.database.DatabaseMetadataReaderRegistry;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.domain.model.Table;
import java.util.Optional;
import com.behsazan.schemaforge.specification.adapter.docx.DocxSpecificationParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.Test;

class ArtifactGenerationServiceTest {
    private final Clock clock = Clock.fixed(Instant.parse("2026-07-20T10:30:25Z"), ZoneOffset.UTC);
    private final ArtifactGenerationService service = new ArtifactGenerationService(
            new DocxSpecificationParser(), ddlEngine(),
            new ZipArtifactPackager(), clock);

    private static DdlGenerationEngine ddlEngine() {
        return new DdlGenerationEngine(
                new DialectRegistry(List.of(new OracleDialect(), new PostgreSqlDialect())),
                new RendererRegistry(List.of(new OracleDdlRenderer(), new PostgreSqlDdlRenderer())),
                new ColumnDefinitionGeneratorRegistry(List.of(
                        new OracleColumnDefinitionGenerator(),
                        new PostgreSqlColumnDefinitionGenerator())));
    }

    @Test
    void missingDatabaseTableProducesOnlySql() throws Exception {
        GeneratedZip result = service.generateFromWord("customer.docx", new ByteArrayInputStream(docx("CUSTOMER")));

        assertThat(result.fileName()).isEqualTo("CUSTOMER-20260720-103025.zip");
        Map<String, byte[]> entries = unzip(result.content());
        assertThat(entries).containsOnlyKeys("CUSTOMER-20260720-103025.sql");
        assertThat(new String(entries.get("CUSTOMER-20260720-103025.sql"), StandardCharsets.UTF_8))
                .contains("CREATE TABLE APP.CUSTOMER")
                .contains("PRIMARY KEY (ID)");
    }

    @Test
    void inputZipProducesOneFlatZipForAllDocuments() throws Exception {
        byte[] input = zipOf(
                Map.entry("folder/customer.docx", docx("CUSTOMER")),
                Map.entry("account.docx", docx("ACCOUNT")));

        GeneratedZip result = service.generateFromZip("input.zip", new ByteArrayInputStream(input));

        assertThat(result.fileName()).isEqualTo("SchemaForge-20260720-103025.zip");
        Map<String, byte[]> entries = unzip(result.content());
        assertThat(entries).containsOnlyKeys(
                "CUSTOMER-20260720-103025.sql",
                "ACCOUNT-20260720-103025.sql");
        assertThat(entries.keySet()).noneMatch(name -> name.endsWith(".zip"));
    }


    @Test
    void existingDatabaseTableProducesOnlyComparisonExcel() throws Exception {
        DatabaseSchemaHolder holder = databaseTable("CUSTOMER");
        DatabaseMetadataReader lookup = new DatabaseMetadataReader() {
            @Override public DatabaseProduct databaseProduct() { return DatabaseProduct.ORACLE; }
            @Override public Optional<Table> readTable(String schema, String table) {
                return Optional.of(holder.table());
            }
        };
        ArtifactGenerationService comparisonService = new ArtifactGenerationService(
                new DocxSpecificationParser(),
                new CanonicalSchemaCompareExcelWriter(),
                ddlEngine(),
                new DatabaseMetadataReaderRegistry(List.of(lookup)),
                null,
                new ZipArtifactPackager(),
                clock);

        GeneratedZip result = comparisonService.generateFromWord(
                "customer.docx", new ByteArrayInputStream(docx("CUSTOMER")));

        Map<String, byte[]> entries = unzip(result.content());
        assertThat(entries).containsOnlyKeys("CUSTOMER_compare_20260720_103025.xlsx");
        assertThat(entries.values().iterator().next()).isNotEmpty();
    }

    private DatabaseSchemaHolder databaseTable(String tableName) throws Exception {
        var schema = new DocxSpecificationParser().parse(new com.behsazan.schemaforge.specification.spi.SpecificationSource(
                "database.docx", new ByteArrayInputStream(docx(tableName))));
        return new DatabaseSchemaHolder(schema.tables().getFirst());
    }

    private record DatabaseSchemaHolder(Table table) { }
    private byte[] docx(String tableName) throws Exception {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            XWPFTable metadata = document.createTable(2, 3);
            metadata.getRow(0).getCell(0).setText("TABLE NAME");
            metadata.getRow(0).getCell(1).setText("SCHEMA");
            metadata.getRow(0).getCell(2).setText("TABLE PURPOSE");
            metadata.getRow(1).getCell(0).setText(tableName);
            metadata.getRow(1).getCell(1).setText("APP");
            metadata.getRow(1).getCell(2).setText(tableName + " table");

            XWPFTable columns = document.createTable(3, 7);
            columns.getRow(0).getCell(0).setText("COLUMN NAME");
            columns.getRow(0).getCell(1).setText("DATA TYPE");
            columns.getRow(0).getCell(2).setText("REQUIRED");
            columns.getRow(0).getCell(3).setText("PRIMARY KEY");
            columns.getRow(0).getCell(4).setText("PERSIAN COLUMN");
            columns.getRow(0).getCell(5).setText("DEFAULT");
            columns.getRow(0).getCell(6).setText("INDEX");

            columns.getRow(1).getCell(0).setText("ID");
            columns.getRow(1).getCell(1).setText("NUMBER(10) IDENTITY");
            columns.getRow(1).getCell(2).setText("Y");
            columns.getRow(1).getCell(3).setText("PK");
            columns.getRow(1).getCell(4).setText("Identifier");

            columns.getRow(2).getCell(0).setText("TITLE");
            columns.getRow(2).getCell(1).setText("VARCHAR2(100)");
            columns.getRow(2).getCell(2).setText("Y");
            columns.getRow(2).getCell(4).setText("Title");
            columns.getRow(2).getCell(6).setText("IX1");

            document.write(output);
            return output.toByteArray();
        }
    }

    @SafeVarargs
    private byte[] zipOf(Map.Entry<String, byte[]>... files) throws Exception {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(output)) {
            for (Map.Entry<String, byte[]> file : files) {
                zip.putNextEntry(new ZipEntry(file.getKey()));
                zip.write(file.getValue());
                zip.closeEntry();
            }
            zip.finish();
            return output.toByteArray();
        }
    }

    private Map<String, byte[]> unzip(byte[] bytes) throws Exception {
        Map<String, byte[]> entries = new LinkedHashMap<>();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                entries.put(entry.getName(), zip.readAllBytes());
                zip.closeEntry();
            }
        }
        return entries;
    }
}
