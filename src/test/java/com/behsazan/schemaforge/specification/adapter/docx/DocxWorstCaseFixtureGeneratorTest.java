package com.behsazan.schemaforge.specification.adapter.docx;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.junit.jupiter.api.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

import java.io.BufferedWriter;
import java.math.BigInteger;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Generates one deliberately malformed table-design DOCX containing a broad set of
 * validation failures. The generated document is intended as a reusable worst-case
 * fixture for parser, validation and Excel comparison tests.
 *
 * <p>Run with:</p>
 * <pre>
 * mvn -Dtest=DocxWorstCaseFixtureGeneratorTest test \
 *   -Dschemaforge.corpus.dir=D:/Specifications/Tables \
 *   -Dschemaforge.worstcase.output=target/schemaforge-worst-case
 * </pre>
 */
class DocxWorstCaseFixtureGeneratorTest {
    private static final String INPUT_DIRECTORY_PROPERTY = "schemaforge.corpus.dir";
    private static final String OUTPUT_DIRECTORY_PROPERTY = "schemaforge.worstcase.output";

    @Test
    void generateWorstCaseTableSpecificationFromCorpusVocabulary() throws Exception {
        Path inputDirectory = Path.of(System.getProperty(INPUT_DIRECTORY_PROPERTY, "src/test/resources/samples/docx"));
        Path outputDirectory = Path.of(System.getProperty(
                OUTPUT_DIRECTORY_PROPERTY,
                "target/schemaforge-worst-case"));

        assertThat(inputDirectory)
                .as("DOCX corpus directory; set -D%s=<directory>", INPUT_DIRECTORY_PROPERTY)
                .isDirectory();

        List<Path> corpusFiles;
        try (var paths = Files.walk(inputDirectory)) {
            corpusFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(this::isDocx)
                    .sorted(Comparator.comparing(Path::toString))
                    .toList();
        }
        assertThat(corpusFiles).isNotEmpty();

        CorpusVocabulary vocabulary = readCorpusVocabulary(corpusFiles);
        Files.createDirectories(outputDirectory);

        Path docx = outputDirectory.resolve("WORST_CASE_TABLE_SPEC.docx");
        Path manifest = outputDirectory.resolve("WORST_CASE_MANIFEST.csv");
        List<DefectRow> defects = worstCaseRows(vocabulary);

        writeWorstCaseDocument(docx, vocabulary, defects, corpusFiles.size());
        writeManifest(manifest, defects);

        assertThat(docx).exists().isRegularFile();
        assertThat(Files.size(docx)).isGreaterThan(0L);
        assertThat(manifest).exists().isRegularFile();
        assertThat(Files.size(manifest)).isGreaterThan(0L);

        try (InputStream input = Files.newInputStream(docx); XWPFDocument generated = new XWPFDocument(input)) {
            assertThat(generated.getTables()).hasSizeGreaterThanOrEqualTo(2);
            XWPFTable columns = generated.getTables().get(1);
            assertThat(columns.getNumberOfRows()).isEqualTo(defects.size() + 1);
            assertThat(columns.getText()).contains("DUPLICATE_FIELD", "MISSING_DATA_TYPE", "SPELLING_ERROR");
        }

        System.out.printf(
                "Worst-case DOCX generated: corpusFiles=%d, defects=%d, output=%s%n",
                corpusFiles.size(), defects.size(), docx.toAbsolutePath());
    }

    private CorpusVocabulary readCorpusVocabulary(List<Path> files) {
        Set<String> names = new LinkedHashSet<>();
        Set<String> types = new LinkedHashSet<>();
        Set<String> descriptions = new LinkedHashSet<>();

        for (Path file : files) {
            try (InputStream input = Files.newInputStream(file); XWPFDocument document = new XWPFDocument(input)) {
                for (XWPFTable table : document.getTables()) {
                    if (table.getNumberOfRows() < 2) {
                        continue;
                    }
                    List<String> headers = table.getRow(0).getTableCells().stream()
                            .map(XWPFTableCell::getText)
                            .map(this::normalize)
                            .toList();
                    int nameIndex = findHeader(headers, "COLUMN NAME", "نام ستون");
                    int typeIndex = findHeader(headers, "DATA TYPE", "DATATYPE", "نوع داده");
                    int descriptionIndex = findHeader(headers, "COLUMN DESCRIPTION", "شرح ستون", "نام فارسی ستون");
                    if (nameIndex < 0 || typeIndex < 0) {
                        continue;
                    }
                    for (int rowIndex = 1; rowIndex < table.getNumberOfRows(); rowIndex++) {
                        XWPFTableRow row = table.getRow(rowIndex);
                        addIfPresent(names, cell(row, nameIndex));
                        addIfPresent(types, cell(row, typeIndex));
                        if (descriptionIndex >= 0) {
                            addIfPresent(descriptions, cell(row, descriptionIndex));
                        }
                    }
                }
            } catch (Exception ignored) {
                // A corrupt source document is itself useful corpus information; continue scanning.
            }
        }

        return new CorpusVocabulary(
                names.stream().findFirst().orElse("CUSTOMER_ID"),
                types.stream().findFirst().orElse("NUMBER(19)"),
                descriptions.stream().findFirst().orElse("شناسه مشتری"));
    }

    private List<DefectRow> worstCaseRows(CorpusVocabulary vocabulary) {
        List<DefectRow> rows = new ArrayList<>();
        rows.add(row("VALID_BASELINE", vocabulary.sampleName(), vocabulary.sampleDescription(), vocabulary.sampleType(), "PK", "", "", "Y", "", "", "", "", "Baseline control row"));
        rows.add(row("DUPLICATE_FIELD", "CUSTOMER_ID", "شناسه مشتری", "NUMBER(19)", "", "", "", "Y", "", "", "", "", "Duplicate name - first occurrence"));
        rows.add(row("DUPLICATE_FIELD", "CUSTOMER_ID", "شناسه تکراری", "NUMBER(19)", "", "", "", "Y", "", "", "", "", "Duplicate name - exact duplicate"));
        rows.add(row("DUPLICATE_CASE", "customer_id", "شناسه با حروف کوچک", "NUMBER(19)", "", "", "", "Y", "", "", "", "", "Duplicate after case normalization"));
        rows.add(row("DUPLICATE_WHITESPACE", " CUSTOMER_ID ", "شناسه با فاصله", "NUMBER(19)", "", "", "", "Y", "", "", "", "", "Duplicate after trim"));
        rows.add(row("MISSING_COLUMN_NAME", "", "ستون بدون نام", "VARCHAR2(50)", "", "", "", "N", "", "", "", "", "Column name omitted"));
        rows.add(row("MISSING_DATA_TYPE", "MISSING_TYPE", "نوع داده ذکر نشده", "", "", "", "", "N", "", "", "", "", "Data type omitted"));
        rows.add(row("MISSING_DESCRIPTION", "NO_DESCRIPTION", "", "VARCHAR2(100)", "", "", "", "N", "", "", "", "", "Description omitted"));
        rows.add(row("SPELLING_ERROR", "CUSTMER_ADRESS", "آدرسس مشترى", "VARCAHR2(200)", "", "", "", "N", "", "", "", "", "Intentional English and Persian spelling errors"));
        rows.add(row("PLURAL_FIELD_NAME", "CUSTOMERS", "مشتریان", "NUMBER(19)", "", "", "", "N", "", "", "", "", "Field name is plural"));
        rows.add(row("PLURAL_FIELD_NAME", "STATUSES", "وضعیت ها", "VARCHAR2(20)", "", "", "", "N", "", "", "", "", "Irregular/plural suffix"));
        rows.add(row("INVALID_IDENTIFIER_SPACE", "CUSTOMER NAME", "نام مشتری", "VARCHAR2(100)", "", "", "", "Y", "", "", "", "", "Identifier contains space"));
        rows.add(row("INVALID_IDENTIFIER_LEADING_DIGIT", "1ST_NAME", "نام اول", "VARCHAR2(100)", "", "", "", "N", "", "", "", "", "Identifier starts with digit"));
        rows.add(row("INVALID_IDENTIFIER_SYMBOL", "CUSTOMER-NAME", "نام مشتری", "VARCHAR2(100)", "", "", "", "N", "", "", "", "", "Identifier contains hyphen"));
        rows.add(row("RESERVED_WORD", "ORDER", "ترتیب", "NUMBER(5)", "", "", "", "N", "", "", "", "", "Database reserved word"));
        rows.add(row("TOO_LONG_IDENTIFIER", "THIS_COLUMN_NAME_IS_INTENTIONALLY_LONGER_THAN_THE_SUPPORTED_DATABASE_IDENTIFIER_LIMIT_123", "نام بیش از حد طولانی", "VARCHAR2(10)", "", "", "", "N", "", "", "", "", "Identifier exceeds common DB limits"));
        rows.add(row("INVISIBLE_CHARACTER", "CUSTOMER\u200B_ID", "شناسه با نویسه نامرئی", "NUMBER(19)", "", "", "", "N", "", "", "", "", "Zero-width character in name"));
        rows.add(row("UNSUPPORTED_DATA_TYPE", "GEOMETRY_VALUE", "نوع ناشناخته", "MAGIC_TYPE(42)", "", "", "", "N", "", "", "", "", "Unsupported type"));
        rows.add(row("MALFORMED_DATA_TYPE", "AMOUNT", "مبلغ", "NUMBER(,2)", "", "", "", "N", "", "", "", "", "Malformed precision"));
        rows.add(row("INVALID_LENGTH", "SHORT_TEXT", "طول نامعتبر", "VARCHAR2(0)", "", "", "", "N", "", "", "", "", "Zero length"));
        rows.add(row("INVALID_PRECISION_SCALE", "RATE", "نرخ", "NUMBER(2,5)", "", "", "", "N", "", "", "", "", "Scale greater than precision"));
        rows.add(row("TYPE_DESCRIPTION_CONFLICT", "BIRTH_DATE", "تاریخ تولد", "NUMBER(19)", "", "", "", "N", "", "", "", "", "Semantic type mismatch"));
        rows.add(row("REQUIRED_MARK_INVALID", "MANDATORY_VALUE", "مقدار اجباری", "VARCHAR2(20)", "", "", "", "MAYBE", "", "", "", "", "Unknown required marker"));
        rows.add(row("NULL_DEFAULT_CONFLICT", "CREATED_AT", "زمان ایجاد", "TIMESTAMP", "", "", "", "Y", "NULL", "", "", "", "Required column has NULL default"));
        rows.add(row("DEFAULT_TYPE_CONFLICT", "TOTAL_COUNT", "تعداد", "NUMBER(10)", "", "", "", "N", "ABC", "", "", "", "Text default for numeric column"));
        rows.add(row("MULTIPLE_PRIMARY_KEYS", "SECOND_ID", "شناسه دوم", "NUMBER(19)", "PK", "", "", "Y", "", "", "", "", "Second independent PK marker"));
        rows.add(row("BAD_FOREIGN_KEY", "UNKNOWN_PARENT_ID", "ارجاع نامعتبر", "NUMBER(19)", "UNKNOWN_SCHEMA.UNKNOWN_TABLE/Y", "", "", "N", "", "", "", "", "Reference to missing table/column"));
        rows.add(row("SELF_REFERENCE_AMBIGUOUS", "PARENT_ID", "والد", "NUMBER(19)", "WORST_CASE_TABLE/Y", "", "", "N", "", "", "", "", "Ambiguous self reference"));
        rows.add(row("DUPLICATE_UNIQUE_POSITION", "EMAIL", "ایمیل", "VARCHAR2(200)", "", "UK1,1", "", "N", "", "", "", "", "Duplicate unique position 1"));
        rows.add(row("DUPLICATE_UNIQUE_POSITION", "PHONE", "تلفن", "VARCHAR2(30)", "", "UK1,1", "", "N", "", "", "", "", "Duplicate unique position 1"));
        rows.add(row("DUPLICATE_INDEX_POSITION", "CITY_ID", "شهر", "NUMBER(19)", "", "", "IX1,1", "N", "", "", "", "", "Duplicate index position 1"));
        rows.add(row("DUPLICATE_INDEX_POSITION", "PROVINCE_ID", "استان", "NUMBER(19)", "", "", "IX1,1", "N", "", "", "", "", "Duplicate index position 1"));
        rows.add(row("INVALID_RANGE", "PERCENT_VALUE", "درصد", "NUMBER(3)", "", "", "", "N", "", "", "200..100", "", "Range lower bound exceeds upper bound"));
        rows.add(row("INVALID_CHECK", "STATUS_CODE", "وضعیت", "VARCHAR2(1)", "", "", "", "N", "", "", "", "STATUS_CODE IN ('A','A')", "Duplicate check values"));
        rows.add(row("CONTRADICTORY_RULES", "ACTIVE_FLAG", "فعال", "CHAR(1)", "", "", "", "Y", "N", "", "", "", "Required flag with contradictory default"));
        rows.add(row("MIXED_LANGUAGE_IDENTIFIER", "شناسه_CUSTOMER", "نام ترکیبی", "NUMBER(19)", "", "", "", "N", "", "", "", "", "Mixed Persian/Latin identifier"));
        rows.add(row("ABBREVIATION_AMBIGUOUS", "CSTMR_NO", "شماره مشتری", "VARCHAR2(30)", "", "", "", "N", "", "", "", "", "Unclear abbreviation"));
        rows.add(row("INCONSISTENT_NAMING", "customerName", "نام مشتری", "VARCHAR2(100)", "", "", "", "N", "", "", "", "", "camelCase mixed with uppercase snake case"));
        rows.add(row("TRAILING_UNDERSCORE", "CUSTOMER_", "نام ناقص", "VARCHAR2(100)", "", "", "", "N", "", "", "", "", "Trailing underscore"));
        rows.add(row("EMPTY_ROW", "", "", "", "", "", "", "", "", "", "", "", "Completely empty logical row"));
        return rows;
    }

    private void writeWorstCaseDocument(
            Path output,
            CorpusVocabulary vocabulary,
            List<DefectRow> defects,
            int corpusFileCount) throws Exception {
        try (XWPFDocument document = new XWPFDocument()) {
            configureLandscapePage(document);
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            var titleRun = title.createRun();
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.setText("SchemaForge - Worst Case Table Specification");

            XWPFParagraph note = document.createParagraph();
            note.createRun().setText(
                    "Generated from vocabulary observed in " + corpusFileCount
                            + " DOCX files. This document is intentionally invalid and must never be deployed.");

            XWPFTable metadata = document.createTable(2, 3);
            setRow(metadata.getRow(0), "TABLE NAME", "SCHEMA", "TABLE DESCRIPTION");
            setRow(metadata.getRow(1), "WORST_CASE_TABLES", "INVALID SCHEMA", "جدول عمدی شامل همه خطاهای محتمل");
            styleHeader(metadata.getRow(0));

            document.createParagraph().createRun().setText(
                    "Corpus examples: name=" + vocabulary.sampleName()
                            + ", type=" + vocabulary.sampleType()
                            + ", description=" + vocabulary.sampleDescription());

            String[] headers = {
                    "SCENARIO", "COLUMN NAME", "COLUMN DESCRIPTION", "DATA TYPE", "KEY",
                    "UNIQUE", "INDEX", "REQUIRED", "DEFAULT VALUE", "RANGE",
                    "CHECK CONSTRAINT", "EXPECTED PROBLEM"
            };
            XWPFTable columns = document.createTable(defects.size() + 1, headers.length);
            setRow(columns.getRow(0), headers);
            styleHeader(columns.getRow(0));
            for (int index = 0; index < defects.size(); index++) {
                DefectRow defect = defects.get(index);
                setRow(columns.getRow(index + 1),
                        defect.scenario(), defect.name(), defect.description(), defect.dataType(),
                        defect.key(), defect.unique(), defect.index(), defect.required(),
                        defect.defaultValue(), defect.range(), defect.checkConstraint(), defect.expectedProblem());
            }

            try (OutputStream stream = Files.newOutputStream(
                    output,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                document.write(stream);
            }
        }
    }

    private void writeManifest(Path output, List<DefectRow> defects) throws Exception {
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writer.write("scenario,column_name,expected_problem");
            writer.newLine();
            for (DefectRow defect : defects) {
                writer.write(csv(defect.scenario()));
                writer.write(',');
                writer.write(csv(defect.name()));
                writer.write(',');
                writer.write(csv(defect.expectedProblem()));
                writer.newLine();
            }
        }
    }

    private void configureLandscapePage(XWPFDocument document) {
        CTSectPr section = document.getDocument().getBody().isSetSectPr()
                ? document.getDocument().getBody().getSectPr()
                : document.getDocument().getBody().addNewSectPr();
        CTPageSz pageSize = section.isSetPgSz() ? section.getPgSz() : section.addNewPgSz();
        pageSize.setOrient(STPageOrientation.LANDSCAPE);
        pageSize.setW(BigInteger.valueOf(15840));
        pageSize.setH(BigInteger.valueOf(12240));
        CTPageMar margins = section.isSetPgMar() ? section.getPgMar() : section.addNewPgMar();
        margins.setTop(BigInteger.valueOf(360));
        margins.setBottom(BigInteger.valueOf(360));
        margins.setLeft(BigInteger.valueOf(360));
        margins.setRight(BigInteger.valueOf(360));
    }

    private void styleHeader(XWPFTableRow row) {
        for (XWPFTableCell cell : row.getTableCells()) {
            cell.getParagraphs().forEach(paragraph -> paragraph.getRuns().forEach(run -> { run.setBold(true); run.setFontSize(7); }));
        }
    }

    private void setRow(XWPFTableRow row, String... values) {
        for (int index = 0; index < values.length; index++) {
            XWPFTableCell cell = row.getCell(index);
            if (cell == null) {
                cell = row.addNewTableCell();
            }
            cell.setText(values[index] == null ? "" : values[index]);
            cell.getParagraphs().forEach(paragraph -> paragraph.getRuns().forEach(run -> run.setFontSize(7)));
        }
    }

    private int findHeader(List<String> headers, String... aliases) {
        for (int index = 0; index < headers.size(); index++) {
            String header = headers.get(index);
            for (String alias : aliases) {
                if (header.contains(normalize(alias))) {
                    return index;
                }
            }
        }
        return -1;
    }

    private String cell(XWPFTableRow row, int index) {
        return index >= 0 && index < row.getTableCells().size() ? row.getCell(index).getText() : null;
    }

    private void addIfPresent(Set<String> target, String value) {
        String normalized = value == null ? "" : value.trim();
        if (!normalized.isBlank()) {
            target.add(normalized);
        }
    }

    private boolean isDocx(Path path) {
        return path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".docx");
    }

    private String normalize(String value) {
        return value == null ? "" : value.replace('\n', ' ').replace('\r', ' ')
                .trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }

    private String csv(String value) {
        String safe = value == null ? "" : value.replace("\r", " ").replace("\n", " ");
        return '"' + safe.replace("\"", "\"\"") + '"';
    }

    private DefectRow row(
            String scenario,
            String name,
            String description,
            String dataType,
            String key,
            String unique,
            String index,
            String required,
            String defaultValue,
            String range,
            String checkConstraint,
            String ignored,
            String expectedProblem) {
        return new DefectRow(scenario, name, description, dataType, key, unique, index,
                required, defaultValue, range, checkConstraint, expectedProblem);
    }

    private record CorpusVocabulary(String sampleName, String sampleType, String sampleDescription) {
    }

    private record DefectRow(
            String scenario,
            String name,
            String description,
            String dataType,
            String key,
            String unique,
            String index,
            String required,
            String defaultValue,
            String range,
            String checkConstraint,
            String expectedProblem) {
    }
}
