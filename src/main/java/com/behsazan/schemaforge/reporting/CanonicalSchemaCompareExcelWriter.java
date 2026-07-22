package com.behsazan.schemaforge.reporting;

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
import java.util.HashMap;
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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

/** Creates the document-versus-live-database comparison workbook from canonical tables. */
@Component
public final class CanonicalSchemaCompareExcelWriter {
    private static final String[] HEADERS = {
            "COLUMN_USAGE", "COLUMN_ID", "COLUMN_NAME", "COMMENTS",
            "DATA_TYPE", "PRIMARY/FOREIGN KEY", "UNIQUE", "INDEX", "REQUIRED",
            "DEFAULT", "RANGE", "COLUMN_ID", "COLUMN_NAME", "DATA_TYPE",
            "NULLABLE", "DATA_DEFAULT", "COMMENTS", "INDEX", "UNIQUE_INDEX",
            "FOREIGN KEY", "CHECK CONSTRAINT", "DIFF"
    };

    public byte[] write(Table documentTable, Table databaseTable, Map<String, Integer> usageCounts) {
        Objects.requireNonNull(documentTable, "documentTable must not be null");
        Objects.requireNonNull(databaseTable, "databaseTable must not be null");
        Map<String, Integer> safeUsageCounts = usageCounts == null ? Map.of() : Map.copyOf(usageCounts);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(safeSheetName(documentTable.qualifiedName().name().value()));
            CellStyle header = headerStyle(workbook);
            CellStyle normal = baseStyle(workbook);
            CellStyle diff = diffStyle(workbook);
            writeHeader(sheet, header);

            List<ColumnPair> pairs = pairColumns(documentTable, databaseTable);
            int rowNumber = 1;
            for (ColumnPair pair : pairs) {
                Row row = sheet.createRow(rowNumber++);
                writeDocument(row, documentTable, pair.document(), usage(safeUsageCounts, pair.document()), normal);
                writeDatabase(row, databaseTable, pair.database(), normal);
                List<String> differences = compare(documentTable, pair.document(), databaseTable, pair.database(), pair.renameCandidate());
                setCell(row, 21, String.join(",", differences), differences.isEmpty() ? normal : diff);
            }

            configure(sheet, pairs.size());
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot create schema comparison Excel", exception);
        }
    }

    private List<ColumnPair> pairColumns(Table documentTable, Table databaseTable) {
        Map<String, Column> doc = byName(documentTable.columns());
        Map<String, Column> db = byName(databaseTable.columns());
        List<ColumnPair> result = new ArrayList<>();
        Set<String> matchedDb = new HashSet<>();
        List<Column> unmatchedDoc = new ArrayList<>();

        documentTable.columns().stream().sorted(byPosition()).forEach(column -> {
            Column exact = db.get(column.name().normalized());
            if (exact != null) {
                result.add(new ColumnPair(column, exact, false));
                matchedDb.add(exact.name().normalized());
            } else {
                unmatchedDoc.add(column);
            }
        });

        List<Column> unmatchedDb = databaseTable.columns().stream()
                .filter(column -> !matchedDb.contains(column.name().normalized()))
                .sorted(byPosition()).collect(Collectors.toCollection(ArrayList::new));

        for (Column documentColumn : unmatchedDoc) {
            Column candidate = bestRenameCandidate(documentColumn, unmatchedDb);
            if (candidate != null) {
                result.add(new ColumnPair(documentColumn, candidate, true));
                unmatchedDb.remove(candidate);
            } else {
                result.add(new ColumnPair(documentColumn, null, false));
            }
        }
        unmatchedDb.forEach(column -> result.add(new ColumnPair(null, column, false)));
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

    private List<String> compare(Table docTable, Column doc, Table dbTable, Column db, boolean renameCandidate) {
        List<String> differences = new ArrayList<>();
        if (doc == null) return List.of("COLUMN_REMOVED_FROM_DOCUMENT");
        if (db == null) return List.of("COLUMN_ADDED_IN_DOCUMENT");
        if (renameCandidate || !doc.name().equals(db.name())) differences.add("POSSIBLE_RENAME");
        if (!normalize(format(doc)).equals(normalize(format(db)))) differences.add("DATA_TYPE");
        if (doc.nullable() != db.nullable()) differences.add("NULLABLE");
        if (doc.identity() != db.identity()) differences.add("IDENTITY");
        if (!normalizeDefault(doc.defaultValue().expression()).equals(normalizeDefault(db.defaultValue().expression()))) {
            differences.add("DEFAULT");
        }
        if (!normalizeText(doc.description().value()).equals(normalizeText(db.description().value()))) differences.add("COMMENTS");
        if (inPrimaryKey(docTable, doc.name()) != inPrimaryKey(dbTable, db.name())) differences.add("PRIMARY_KEY");
        if (inUnique(docTable, doc.name()) != inUnique(dbTable, db.name())) differences.add("UNIQUE");
        if (!indexSignatures(docTable, doc.name()).equals(indexSignatures(dbTable, db.name()))) differences.add("INDEX");
        if (!foreignKeySignatures(docTable, doc.name()).equals(foreignKeySignatures(dbTable, db.name()))) differences.add("FOREIGN_KEY");
        if (!checkExpressions(docTable, doc.name()).equals(checkExpressions(dbTable, db.name()))) differences.add("CHECK_CONSTRAINT");
        return differences;
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

    private String normalize(String value) { return value == null ? "" : value.trim().toUpperCase(Locale.ROOT).replaceAll("\\s+", ""); }
    private String normalizeText(String value) { return value == null ? "" : value.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT); }
    private String normalizeDefault(String value) {
        String normalized = normalize(value);
        while (normalized.startsWith("(") && normalized.endsWith(")") && normalized.length() > 1) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }
        return normalized;
    }

    private void writeHeader(Sheet sheet, CellStyle style) {
        Row row = sheet.createRow(0);
        row.setHeightInPoints(32);
        for (int i = 0; i < HEADERS.length; i++) setCell(row, i, HEADERS[i], style);
    }

    private void configure(Sheet sheet, int rows) {
        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, Math.max(1, rows), 0, HEADERS.length - 1));
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + 512, 16000));
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
        String candidate = value == null || value.isBlank() ? "Comparison" : value.trim();
        candidate = candidate.replaceAll("[\\\\/?*\\[\\]:]", "_");
        return candidate.substring(0, Math.min(candidate.length(), 31));
    }

    private record ColumnPair(Column document, Column database, boolean renameCandidate) { }
}
