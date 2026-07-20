package com.behsazan.schemaforge.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.behsazan.schemaforge.generation.oracle.OracleDdlGenerator;
import com.behsazan.schemaforge.packaging.ZipArtifactPackager;
import com.behsazan.schemaforge.reporting.SchemaExcelWriter;
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
            new DocxSpecificationParser(), new SchemaExcelWriter(), new OracleDdlGenerator(),
            new ZipArtifactPackager(), clock);

    @Test
    void wordProducesOneZipWithSqlAndExcelNamedByTableAndTimestamp() throws Exception {
        GeneratedZip result = service.generateFromWord("customer.docx", new ByteArrayInputStream(docx("CUSTOMER")));

        assertThat(result.fileName()).isEqualTo("CUSTOMER-20260720-103025.zip");
        Map<String, byte[]> entries = unzip(result.content());
        assertThat(entries).containsOnlyKeys(
                "CUSTOMER-20260720-103025.sql",
                "CUSTOMER-20260720-103025.xlsx");
        assertThat(new String(entries.get("CUSTOMER-20260720-103025.sql"), StandardCharsets.UTF_8))
                .contains("CREATE TABLE APP.CUSTOMER")
                .contains("PRIMARY KEY (ID)");
        assertThat(entries.get("CUSTOMER-20260720-103025.xlsx")).isNotEmpty();
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
                "CUSTOMER-20260720-103025.xlsx",
                "ACCOUNT-20260720-103025.sql",
                "ACCOUNT-20260720-103025.xlsx");
        assertThat(entries.keySet()).noneMatch(name -> name.endsWith(".zip"));
    }

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
