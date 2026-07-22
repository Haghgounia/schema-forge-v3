package com.behsazan.schemaforge.reporting;

import com.behsazan.schemaforge.comparison.column.ColumnComparisonRule;
import com.behsazan.schemaforge.comparison.column.CommentComparisonRule;
import com.behsazan.schemaforge.comparison.column.DataTypeNameComparisonRule;
import com.behsazan.schemaforge.comparison.column.DefaultComparisonRule;
import com.behsazan.schemaforge.comparison.column.IdentityComparisonRule;
import com.behsazan.schemaforge.comparison.column.LengthComparisonRule;
import com.behsazan.schemaforge.comparison.column.NullableComparisonRule;
import com.behsazan.schemaforge.comparison.column.PrecisionComparisonRule;
import com.behsazan.schemaforge.comparison.column.ScaleComparisonRule;
import com.behsazan.schemaforge.comparison.context.ComparisonContextFactory;
import com.behsazan.schemaforge.comparison.engine.SchemaComparisonEngine;
import com.behsazan.schemaforge.comparison.model.ComparisonDifference;
import com.behsazan.schemaforge.comparison.model.DifferenceScope;
import com.behsazan.schemaforge.comparison.model.DifferenceSeverity;
import com.behsazan.schemaforge.comparison.model.DifferenceType;
import com.behsazan.schemaforge.comparison.model.TableComparisonReport;
import com.behsazan.schemaforge.comparison.rule.ColumnDefinitionComparisonRule;
import com.behsazan.schemaforge.comparison.rule.ColumnExistenceComparisonRule;
import com.behsazan.schemaforge.comparison.rule.constraint.CheckConstraintComparisonRule;
import com.behsazan.schemaforge.comparison.rule.constraint.ForeignKeyComparisonRule;
import com.behsazan.schemaforge.comparison.rule.constraint.PrimaryKeyComparisonRule;
import com.behsazan.schemaforge.comparison.rule.constraint.UniqueKeyComparisonRule;
import com.behsazan.schemaforge.comparison.rule.index.IndexComparisonRule;
import com.behsazan.schemaforge.comparison.signature.CheckConstraintSignatureFactory;
import com.behsazan.schemaforge.comparison.signature.ForeignKeySignatureFactory;
import com.behsazan.schemaforge.comparison.signature.IndexSignatureFactory;
import com.behsazan.schemaforge.comparison.signature.PrimaryKeySignatureFactory;
import com.behsazan.schemaforge.comparison.signature.UniqueKeySignatureFactory;
import com.behsazan.schemaforge.domain.model.CheckConstraint;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.ForeignKey;
import com.behsazan.schemaforge.domain.model.Index;
import com.behsazan.schemaforge.domain.model.IndexColumn;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.model.UniqueKey;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Creates a table-focused comparison workbook from canonical document and database tables. */
@Component
public final class CanonicalSchemaCompareExcelWriter {
    private static final String[] DETAIL_HEADERS = {
            "COLUMN_USAGE", "DOC_COLUMN_ID", "DOC_COLUMN_NAME", "DOC_COMMENTS",
            "DOC_DATA_TYPE", "DOC_KEY", "DOC_UNIQUE", "DOC_INDEX", "DOC_REQUIRED",
            "DOC_DEFAULT", "DOC_RANGE", "DB_COLUMN_ID", "DB_COLUMN_NAME", "DB_DATA_TYPE",
            "DB_NULLABLE", "DB_DEFAULT", "DB_COMMENTS", "DB_INDEX", "DB_UNIQUE",
            "DB_FOREIGN_KEY", "DB_CHECK_CONSTRAINT", "DIFF"
    };
    private static final String[] DIFFERENCE_HEADERS = {
            "CODE", "SCHEMA", "TABLE", "SCOPE", "OBJECT", "PROPERTY", "DIFFERENCE_TYPE",
            "EXPECTED_VALUE", "ACTUAL_VALUE", "SEVERITY", "RECOMMENDATION", "RESOLUTION", "MESSAGE"
    };

    private final SchemaComparisonEngine comparisonEngine;

    public CanonicalSchemaCompareExcelWriter() {
        this(defaultEngine());
    }

    @Autowired
    public CanonicalSchemaCompareExcelWriter(SchemaComparisonEngine comparisonEngine) {
        this.comparisonEngine = Objects.requireNonNull(comparisonEngine, "comparisonEngine must not be null");
    }

    public byte[] write(Table documentTable, Table databaseTable, Map<String, Integer> usageCounts) {
        Objects.requireNonNull(documentTable, "documentTable must not be null");
        Objects.requireNonNull(databaseTable, "databaseTable must not be null");
        Map<String, Integer> safeUsageCounts = usageCounts == null ? Map.of() : Map.copyOf(usageCounts);
        TableComparisonReport report = comparisonEngine.compare(documentTable, databaseTable);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            CellStyle header = headerStyle(workbook);
            CellStyle normal = baseStyle(workbook);
            CellStyle diff = diffStyle(workbook);
            Map<DifferenceSeverity, CellStyle> severityStyles = severityStyles(workbook);

            String detailSheetName = safeSheetName(documentTable.qualifiedName().name().value());
            writeSummarySheet(workbook, documentTable, databaseTable, report, detailSheetName, header, normal, severityStyles);
            writeDetailSheet(workbook, documentTable, databaseTable, safeUsageCounts, report, detailSheetName, header, normal, diff);
            writeDifferencesSheet(workbook, report, detailSheetName, header, normal, severityStyles);

            workbook.setActiveSheet(0);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot create schema comparison Excel", exception);
        }
    }

    private void writeSummarySheet(
            XSSFWorkbook workbook,
            Table documentTable,
            Table databaseTable,
            TableComparisonReport report,
            String detailSheetName,
            CellStyle header,
            CellStyle normal,
            Map<DifferenceSeverity, CellStyle> severityStyles) {
        Sheet sheet = workbook.createSheet("TABLE_SUMMARY");

        Row title = sheet.createRow(0);
        title.setHeightInPoints(30);
        setCell(title, 0, "SchemaForge Table Comparison Report", titleStyle(workbook));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        Row headerRow = sheet.createRow(2);
        setCell(headerRow, 0, "METRIC", header);
        setCell(headerRow, 1, "VALUE", header);
        setCell(headerRow, 2, "NAVIGATION", header);

        String[][] rows = {
                {"SCHEMA", report.schemaName()},
                {"TABLE", report.tableName()},
                {"STATUS", report.hasDifferences() ? "DIFFERENT" : "EQUAL"},
                {"DOCUMENT_COLUMNS", String.valueOf(documentTable.columns().size())},
                {"DATABASE_COLUMNS", String.valueOf(databaseTable.columns().size())},
                {"TOTAL_DIFFERENCES", String.valueOf(report.summary().total())},
                {"CRITICAL", String.valueOf(report.summary().critical())},
                {"HIGH", String.valueOf(report.summary().high())},
                {"MEDIUM", String.valueOf(report.summary().medium())},
                {"LOW", String.valueOf(report.summary().low())},
                {"INFO", String.valueOf(report.summary().info())}
        };
        for (int i = 0; i < rows.length; i++) {
            Row row = sheet.createRow(i + 3);
            setCell(row, 0, rows[i][0], normal);
            CellStyle valueStyle = switch (rows[i][0]) {
                case "CRITICAL" -> severityStyles.get(DifferenceSeverity.CRITICAL);
                case "HIGH" -> severityStyles.get(DifferenceSeverity.HIGH);
                case "MEDIUM" -> severityStyles.get(DifferenceSeverity.MEDIUM);
                case "LOW" -> severityStyles.get(DifferenceSeverity.LOW);
                case "INFO" -> severityStyles.get(DifferenceSeverity.INFO);
                default -> normal;
            };
            setCell(row, 1, rows[i][1], valueStyle);
        }

        setDocumentLink(workbook, sheet.getRow(4).createCell(2), detailSheetName, "Open table details", normal);
        setDocumentLink(workbook, sheet.getRow(8).createCell(2), "DIFFERENCES", "Open all differences", normal);

        int legendStart = 16;
        Row legendTitle = sheet.createRow(legendStart);
        setCell(legendTitle, 0, "SEVERITY LEGEND", header);
        setCell(legendTitle, 1, "MEANING", header);
        String[][] legend = {
                {"CRITICAL", "Structural integrity or key definition risk"},
                {"HIGH", "Data compatibility or behavior risk"},
                {"MEDIUM", "Definition mismatch requiring review"},
                {"LOW", "Documentation or descriptive mismatch"},
                {"INFO", "Informational difference"}
        };
        for (int i = 0; i < legend.length; i++) {
            Row row = sheet.createRow(legendStart + 1 + i);
            DifferenceSeverity severity = DifferenceSeverity.valueOf(legend[i][0]);
            setCell(row, 0, legend[i][0], severityStyles.get(severity));
            setCell(row, 1, legend[i][1], normal);
        }

        sheet.createFreezePane(0, 3);
        sheet.setAutoFilter(new CellRangeAddress(2, 13, 0, 1));
        sheet.setColumnWidth(0, 7000);
        sheet.setColumnWidth(1, 10000);
        sheet.setColumnWidth(2, 7000);
    }

    private void writeDetailSheet(
            XSSFWorkbook workbook,
            Table documentTable,
            Table databaseTable,
            Map<String, Integer> usageCounts,
            TableComparisonReport report,
            String detailSheetName,
            CellStyle header,
            CellStyle normal,
            CellStyle diff) {
        Sheet sheet = workbook.createSheet(detailSheetName);
        writeHeader(sheet, DETAIL_HEADERS, header);
        Map<String, List<ComparisonDifference>> columnDifferences = report.differences().stream()
                .filter(item -> item.scope() == DifferenceScope.COLUMN)
                .collect(Collectors.groupingBy(
                        item -> normalize(item.objectName()),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<ColumnPair> pairs = pairColumns(documentTable, databaseTable);
        int rowNumber = 1;
        for (ColumnPair pair : pairs) {
            Row row = sheet.createRow(rowNumber++);
            writeDocument(row, documentTable, pair.document(), usage(usageCounts, pair.document()), normal);
            writeDatabase(row, databaseTable, pair.database(), normal);
            List<String> types = differencesForPair(pair, columnDifferences);
            setCell(row, 21, String.join(",", types), types.isEmpty() ? normal : diff);
        }
        configure(sheet, DETAIL_HEADERS.length, pairs.size());
    }

    private void writeDifferencesSheet(
            XSSFWorkbook workbook,
            TableComparisonReport report,
            String detailSheetName,
            CellStyle header,
            CellStyle normal,
            Map<DifferenceSeverity, CellStyle> severityStyles) {
        Sheet sheet = workbook.createSheet("DIFFERENCES");
        writeHeader(sheet, DIFFERENCE_HEADERS, header);
        int rowNumber = 1;
        for (ComparisonDifference difference : report.differences()) {
            Row row = sheet.createRow(rowNumber++);
            setCell(row, 0, differenceCode(difference.type()), normal);
            setCell(row, 1, report.schemaName(), normal);
            setDocumentLink(workbook, row.createCell(2), detailSheetName, report.tableName(), normal);
            setCell(row, 3, difference.scope().name(), normal);
            setCell(row, 4, difference.objectName(), normal);
            setCell(row, 5, difference.property(), normal);
            setCell(row, 6, difference.type().name(), normal);
            setCell(row, 7, difference.expectedValue(), normal);
            setCell(row, 8, difference.actualValue(), normal);
            setCell(row, 9, difference.severity().name(), severityStyles.get(difference.severity()));
            setCell(row, 10, recommendation(difference), normal);
            setCell(row, 11, difference.resolutionStrategy().name(), normal);
            setCell(row, 12, difference.message(), normal);
        }
        configure(sheet, DIFFERENCE_HEADERS.length, report.differences().size());
        sheet.setColumnWidth(10, 12000);
        sheet.setColumnWidth(12, 18000);
    }

    private List<String> differencesForPair(
            ColumnPair pair,
            Map<String, List<ComparisonDifference>> differencesByColumn) {
        Set<String> result = new java.util.LinkedHashSet<>();
        if (pair.document() != null) {
            differencesByColumn.getOrDefault(pair.document().name().normalized(), List.of())
                    .forEach(item -> result.add(item.type().name()));
        }
        if (pair.database() != null) {
            differencesByColumn.getOrDefault(pair.database().name().normalized(), List.of())
                    .forEach(item -> result.add(item.type().name()));
        }
        if (pair.renameCandidate()) result.add("POSSIBLE_COLUMN_RENAME");
        return List.copyOf(result);
    }

    private List<ColumnPair> pairColumns(Table documentTable, Table databaseTable) {
        Map<String, Column> database = byName(databaseTable.columns());
        List<ColumnPair> result = new ArrayList<>();
        Set<String> matchedDatabase = new HashSet<>();
        List<Column> unmatchedDocument = new ArrayList<>();

        documentTable.columns().stream().sorted(byPosition()).forEach(column -> {
            Column exact = database.get(column.name().normalized());
            if (exact != null) {
                result.add(new ColumnPair(column, exact, false));
                matchedDatabase.add(exact.name().normalized());
            } else {
                unmatchedDocument.add(column);
            }
        });

        List<Column> unmatchedDatabase = databaseTable.columns().stream()
                .filter(column -> !matchedDatabase.contains(column.name().normalized()))
                .sorted(byPosition()).collect(Collectors.toCollection(ArrayList::new));

        for (Column documentColumn : unmatchedDocument) {
            Column candidate = bestRenameCandidate(documentColumn, unmatchedDatabase);
            if (candidate != null) {
                result.add(new ColumnPair(documentColumn, candidate, true));
                unmatchedDatabase.remove(candidate);
            } else {
                result.add(new ColumnPair(documentColumn, null, false));
            }
        }
        unmatchedDatabase.forEach(column -> result.add(new ColumnPair(null, column, false)));
        return result;
    }

    private Column bestRenameCandidate(Column documentColumn, List<Column> candidates) {
        Column best = null;
        double bestScore = 0.0;
        for (Column candidate : candidates) {
            if (!normalize(format(documentColumn)).equals(normalize(format(candidate)))) continue;
            double nameScore = similarity(documentColumn.name().normalized(), candidate.name().normalized());
            int docPos = documentColumn.ordinalPosition() == null ? Integer.MAX_VALUE : documentColumn.ordinalPosition();
            int dbPos = candidate.ordinalPosition() == null ? Integer.MAX_VALUE : candidate.ordinalPosition();
            double positionScore = docPos == Integer.MAX_VALUE || dbPos == Integer.MAX_VALUE
                    ? 0.0 : 1.0 / (1.0 + Math.abs(docPos - dbPos));
            double commentScore = similarity(normalizeText(documentColumn.description().value()),
                    normalizeText(candidate.description().value()));
            double score = nameScore * 0.65 + positionScore * 0.20 + commentScore * 0.15;
            if (nameScore >= 0.55 && score > bestScore) {
                best = candidate;
                bestScore = score;
            }
        }
        return bestScore >= 0.60 ? best : null;
    }

    private void writeDocument(Row row, Table table, Column column, int usage, CellStyle style) {
        if (column == null) { fill(row, 0, 10, style); return; }
        setCell(row, 0, usage, style);
        setCell(row, 1, column.ordinalPosition(), style);
        setCell(row, 2, column.name().value(), style);
        setCell(row, 3, column.description().value(), style);
        setCell(row, 4, format(column), style);
        setCell(row, 5, key(table, column.name()), style);
        setCell(row, 6, inUnique(table, column.name()) ? "Y" : "N", style);
        setCell(row, 7, String.join(",", indexSignatures(table, column.name())), style);
        setCell(row, 8, column.nullable() ? "N" : "Y", style);
        setCell(row, 9, column.defaultValue().expression(), style);
        setCell(row, 10, String.join("; ", checkExpressions(table, column.name())), style);
    }

    private void writeDatabase(Row row, Table table, Column column, CellStyle style) {
        if (column == null) { fill(row, 11, 20, style); return; }
        setCell(row, 11, column.ordinalPosition(), style);
        setCell(row, 12, column.name().value(), style);
        setCell(row, 13, format(column), style);
        setCell(row, 14, column.nullable() ? "Y" : "N", style);
        setCell(row, 15, column.defaultValue().expression(), style);
        setCell(row, 16, column.description().value(), style);
        setCell(row, 17, String.join(",", indexSignatures(table, column.name())), style);
        setCell(row, 18, inUnique(table, column.name()) ? "Y" : "N", style);
        setCell(row, 19, String.join(",", foreignKeySignatures(table, column.name())), style);
        setCell(row, 20, String.join("; ", checkExpressions(table, column.name())), style);
    }

    private String key(Table table, Identifier column) {
        if (inPrimaryKey(table, column)) return "PK";
        return String.join(",", foreignKeySignatures(table, column));
    }

    private boolean inPrimaryKey(Table table, Identifier column) {
        return table.primaryKey().map(pk -> pk.columns().contains(column)).orElse(false);
    }

    private boolean inUnique(Table table, Identifier column) {
        if (inPrimaryKey(table, column)) return true;
        return table.uniqueKeys().stream().map(UniqueKey::columns).anyMatch(columns -> columns.contains(column))
                || table.indexes().stream().filter(index -> index.type().name().contains("UNIQUE"))
                .anyMatch(index -> index.columns().stream().map(IndexColumn::column).anyMatch(column::equals));
    }

    private Set<String> indexSignatures(Table table, Identifier column) {
        return table.indexes().stream()
                .filter(index -> index.columns().stream().map(IndexColumn::column).anyMatch(column::equals))
                .map(this::indexSignature).collect(Collectors.toCollection(java.util.TreeSet::new));
    }

    private String indexSignature(Index index) {
        return index.name().value() + "(" + index.columns().stream()
                .map(item -> item.column().value() + " " + item.direction().name())
                .collect(Collectors.joining(",")) + ")" + (index.type().name().contains("UNIQUE") ? " UNIQUE" : "");
    }

    private Set<String> foreignKeySignatures(Table table, Identifier column) {
        return table.foreignKeys().stream().filter(fk -> fk.columns().contains(column))
                .map(this::foreignKeySignature).collect(Collectors.toCollection(java.util.TreeSet::new));
    }

    private String foreignKeySignature(ForeignKey fk) {
        String name = fk.name() == null ? "" : fk.name().value();
        return name + "->" + fk.referencedTable() + "(" + fk.referencedColumns().stream()
                .map(Identifier::value).collect(Collectors.joining(",")) + ")"
                + "[DELETE=" + fk.onDelete() + ",UPDATE=" + fk.onUpdate() + "]";
    }

    private Set<String> checkExpressions(Table table, Identifier column) {
        String token = column.normalized();
        return table.checkConstraints().stream().map(CheckConstraint::expression)
                .filter(expression -> normalize(expression).contains(token))
                .map(this::normalizeDefault).collect(Collectors.toCollection(java.util.TreeSet::new));
    }

    private Map<String, Column> byName(List<Column> columns) {
        Map<String, Column> map = new LinkedHashMap<>();
        columns.forEach(column -> map.put(column.name().normalized(), column));
        return map;
    }

    private Comparator<Column> byPosition() {
        return Comparator.comparing(Column::ordinalPosition, Comparator.nullsLast(Integer::compareTo));
    }

    private String format(Column column) {
        var type = column.dataType();
        String name = type.name().value().toUpperCase(Locale.ROOT);
        if (type.length() != null) return name + "(" + type.length() + ")";
        if (type.precision() != null) return type.scale() == null || type.scale() == 0
                ? name + "(" + type.precision() + ")"
                : name + "(" + type.precision() + "," + type.scale() + ")";
        return name;
    }

    private int usage(Map<String, Integer> usage, Column column) {
        if (column == null) return 0;
        return usage.entrySet().stream().filter(entry -> normalize(entry.getKey()).equals(column.name().normalized()))
                .map(Map.Entry::getValue).filter(Objects::nonNull).findFirst().orElse(0);
    }

    private double similarity(String left, String right) {
        if (left.equals(right)) return 1.0;
        if (left.isBlank() || right.isBlank()) return 0.0;
        int distance = levenshtein(left, right);
        return 1.0 - ((double) distance / Math.max(left.length(), right.length()));
    }

    private int levenshtein(String left, String right) {
        int[] previous = new int[right.length() + 1];
        for (int j = 0; j <= right.length(); j++) previous[j] = j;
        for (int i = 1; i <= left.length(); i++) {
            int[] current = new int[right.length() + 1];
            current[0] = i;
            for (int j = 1; j <= right.length(); j++) {
                int cost = left.charAt(i - 1) == right.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(Math.min(current[j - 1] + 1, previous[j] + 1), previous[j - 1] + cost);
            }
            previous = current;
        }
        return previous[right.length()];
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT).replaceAll("\\s+", "");
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }

    private String normalizeDefault(String value) {
        String normalized = normalize(value);
        while (normalized.startsWith("(") && normalized.endsWith(")") && normalized.length() > 1) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }
        return normalized;
    }

    private String differenceCode(DifferenceType type) {
        return switch (type) {
            case TABLE_NOT_FOUND -> "CMP-001";
            case TABLE_COMMENT_CHANGED -> "CMP-002";
            case COLUMN_MISSING -> "CMP-101";
            case COLUMN_EXTRA -> "CMP-102";
            case POSSIBLE_COLUMN_RENAME -> "CMP-103";
            case DATA_TYPE_CHANGED -> "CMP-104";
            case LENGTH_CHANGED -> "CMP-105";
            case PRECISION_CHANGED -> "CMP-106";
            case SCALE_CHANGED -> "CMP-107";
            case NULLABLE_CHANGED -> "CMP-108";
            case DEFAULT_CHANGED -> "CMP-109";
            case COMMENT_CHANGED -> "CMP-110";
            case IDENTITY_CHANGED -> "CMP-111";
            case PRIMARY_KEY_MISSING -> "CMP-201";
            case PRIMARY_KEY_EXTRA -> "CMP-202";
            case PRIMARY_KEY_CHANGED -> "CMP-203";
            case FOREIGN_KEY_MISSING -> "CMP-211";
            case FOREIGN_KEY_EXTRA -> "CMP-212";
            case FOREIGN_KEY_CHANGED -> "CMP-213";
            case UNIQUE_MISSING -> "CMP-221";
            case UNIQUE_EXTRA -> "CMP-222";
            case UNIQUE_CHANGED -> "CMP-223";
            case CHECK_MISSING -> "CMP-231";
            case CHECK_EXTRA -> "CMP-232";
            case CHECK_CHANGED -> "CMP-233";
            case INDEX_MISSING -> "CMP-301";
            case INDEX_EXTRA -> "CMP-302";
            case INDEX_CHANGED -> "CMP-303";
        };
    }

    private String recommendation(ComparisonDifference difference) {
        return switch (difference.type()) {
            case TABLE_NOT_FOUND -> "Create the missing table or verify the selected schema";
            case TABLE_COMMENT_CHANGED, COMMENT_CHANGED -> "Synchronize the database comment with the document";
            case COLUMN_MISSING -> "Add the missing database column";
            case COLUMN_EXTRA -> "Confirm whether the extra database column should be retained or removed";
            case POSSIBLE_COLUMN_RENAME -> "Review the possible rename before changing either side";
            case DATA_TYPE_CHANGED -> "Review compatibility, then alter the column data type";
            case LENGTH_CHANGED, PRECISION_CHANGED, SCALE_CHANGED -> "Review data-loss risk, then alter the column size";
            case NULLABLE_CHANGED -> "Review existing data, then modify nullability";
            case DEFAULT_CHANGED -> "Alter or remove the column default";
            case IDENTITY_CHANGED -> "Review identity or generated-column behavior";
            case PRIMARY_KEY_MISSING, PRIMARY_KEY_EXTRA, PRIMARY_KEY_CHANGED -> "Reconcile the primary key definition";
            case FOREIGN_KEY_MISSING, FOREIGN_KEY_EXTRA, FOREIGN_KEY_CHANGED -> "Reconcile the foreign key definition and actions";
            case UNIQUE_MISSING, UNIQUE_EXTRA, UNIQUE_CHANGED -> "Reconcile the unique constraint definition";
            case CHECK_MISSING, CHECK_EXTRA, CHECK_CHANGED -> "Reconcile the check constraint expression";
            case INDEX_MISSING, INDEX_EXTRA, INDEX_CHANGED -> "Review and reconcile the supporting index";
        };
    }

    private void setDocumentLink(
            XSSFWorkbook workbook,
            Cell cell,
            String targetSheet,
            String label,
            CellStyle style) {
        cell.setCellValue(label);
        var hyperlink = workbook.getCreationHelper().createHyperlink(HyperlinkType.DOCUMENT);
        hyperlink.setAddress("'" + targetSheet.replace("'", "''") + "'!A1");
        cell.setHyperlink(hyperlink);
        cell.setCellStyle(style);
    }

    private Map<DifferenceSeverity, CellStyle> severityStyles(XSSFWorkbook workbook) {
        Map<DifferenceSeverity, CellStyle> result = new java.util.EnumMap<>(DifferenceSeverity.class);
        result.put(DifferenceSeverity.CRITICAL, filledStyle(workbook, IndexedColors.RED));
        result.put(DifferenceSeverity.HIGH, filledStyle(workbook, IndexedColors.ORANGE));
        result.put(DifferenceSeverity.MEDIUM, filledStyle(workbook, IndexedColors.LIGHT_YELLOW));
        result.put(DifferenceSeverity.LOW, filledStyle(workbook, IndexedColors.LIGHT_CORNFLOWER_BLUE));
        result.put(DifferenceSeverity.INFO, filledStyle(workbook, IndexedColors.GREY_25_PERCENT));
        return result;
    }

    private CellStyle filledStyle(XSSFWorkbook workbook, IndexedColors color) {
        CellStyle style = baseStyle(workbook);
        style.setFillForegroundColor(color.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle titleStyle(XSSFWorkbook workbook) {
        CellStyle style = baseStyle(workbook);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private void writeHeader(Sheet sheet, String[] headers, CellStyle style) {
        Row row = sheet.createRow(0);
        row.setHeightInPoints(32);
        for (int i = 0; i < headers.length; i++) setCell(row, i, headers[i], style);
    }

    private void configure(Sheet sheet, int columns, int rows) {
        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, Math.max(1, rows), 0, columns - 1));
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + 512, 18000));
        }
    }

    private CellStyle headerStyle(XSSFWorkbook workbook) {
        CellStyle style = baseStyle(workbook);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle diffStyle(XSSFWorkbook workbook) {
        CellStyle style = baseStyle(workbook);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle baseStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private void fill(Row row, int from, int to, CellStyle style) {
        for (int i = from; i <= to; i++) setCell(row, i, "", style);
    }

    private void setCell(Row row, int index, Object value, CellStyle style) {
        Cell cell = row.createCell(index);
        if (value instanceof Number number) cell.setCellValue(number.doubleValue());
        else cell.setCellValue(value == null ? "" : value.toString());
        cell.setCellStyle(style);
    }

    private String safeSheetName(String value) {
        String candidate = value == null || value.isBlank() ? "TABLE_DETAILS" : value.trim();
        candidate = candidate.replaceAll("[\\\\/?*\\[\\]:]", "_");
        if (candidate.equalsIgnoreCase("TABLE_SUMMARY") || candidate.equalsIgnoreCase("DIFFERENCES")) {
            candidate = candidate + "_DETAIL";
        }
        return candidate.substring(0, Math.min(candidate.length(), 31));
    }

    private static SchemaComparisonEngine defaultEngine() {
        List<ColumnComparisonRule> columnRules = List.of(
                new DataTypeNameComparisonRule(),
                new LengthComparisonRule(),
                new PrecisionComparisonRule(),
                new ScaleComparisonRule(),
                new NullableComparisonRule(),
                new DefaultComparisonRule(),
                new IdentityComparisonRule(),
                new CommentComparisonRule());
        return new SchemaComparisonEngine(
                new ComparisonContextFactory(),
                List.of(
                        new ColumnExistenceComparisonRule(),
                        new ColumnDefinitionComparisonRule(columnRules),
                        new PrimaryKeyComparisonRule(new PrimaryKeySignatureFactory()),
                        new UniqueKeyComparisonRule(new UniqueKeySignatureFactory()),
                        new ForeignKeyComparisonRule(new ForeignKeySignatureFactory()),
                        new CheckConstraintComparisonRule(new CheckConstraintSignatureFactory()),
                        new IndexComparisonRule(new IndexSignatureFactory())));
    }

    private record ColumnPair(Column document, Column database, boolean renameCandidate) { }
}
