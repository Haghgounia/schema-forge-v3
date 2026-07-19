package com.behsazan.schemaforge.reporting;

import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.ForeignKey;
import com.behsazan.schemaforge.domain.model.Index;
import com.behsazan.schemaforge.domain.model.IndexColumn;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.model.UniqueKey;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.specification.domain.ColumnDefinition;
import com.behsazan.schemaforge.specification.domain.DataTypeDefinition;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SchemaCompareExcelWriter {

    private static final String[] HEADERS = {
            "COLUMN_USAGE", "DOC_COLUMN_ID", "DOC_COLUMN_NAME", "DOC_COMMENTS",
            "DOC_DATA_TYPE", "DOC_KEY", "DOC_UNIQUE", "DOC_INDEX", "DOC_REQUIRED",
            "DOC_DEFAULT", "DB_COLUMN_ID", "DB_COLUMN_NAME", "DB_DATA_TYPE",
            "DB_NULLABLE", "DB_DEFAULT", "DB_COMMENTS", "DB_INDEX", "DB_UNIQUE",
            "DB_FOREIGN_KEY", "DIFF"
    };

    public byte[] write(TableDefinition documentTable, Table databaseTable) {
        return write(documentTable, databaseTable, Map.of());
    }

    public byte[] write(
            TableDefinition documentTable,
            Table databaseTable,
            Map<String, Integer> columnUsageCounts) {

        Objects.requireNonNull(documentTable, "documentTable must not be null");
        Objects.requireNonNull(databaseTable, "databaseTable must not be null");
        columnUsageCounts = columnUsageCounts == null ? Map.of() : Map.copyOf(columnUsageCounts);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(safeSheetName(documentTable.name()));
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle normalStyle = createNormalStyle(workbook);
            CellStyle diffStyle = createDiffStyle(workbook);

            writeHeader(sheet, headerStyle);

            Map<String, ColumnDefinition> documentColumns = documentTable.columns().stream()
                    .collect(Collectors.toMap(
                            column -> normalize(column.name()),
                            Function.identity(),
                            (first, second) -> first,
                            LinkedHashMap::new));

            Map<String, Column> databaseColumns = databaseTable.columns().stream()
                    .collect(Collectors.toMap(
                            column -> column.name().normalized(),
                            Function.identity(),
                            (first, second) -> first,
                            LinkedHashMap::new));

            List<String> orderedNames = orderedNames(documentTable, databaseTable);
            int rowNumber = 1;

            for (String normalizedName : orderedNames) {
                ColumnDefinition documentColumn = documentColumns.get(normalizedName);
                Column databaseColumn = databaseColumns.get(normalizedName);
                Row row = sheet.createRow(rowNumber++);

                writeDocumentSide(
                        row,
                        documentTable,
                        documentColumn,
                        findUsageCount(columnUsageCounts, normalizedName),
                        normalStyle);
                writeDatabaseSide(row, databaseTable, databaseColumn, normalStyle);

                List<String> differences = compare(documentTable, documentColumn, databaseTable, databaseColumn);
                setCell(row, 19, String.join(",", differences), differences.isEmpty() ? normalStyle : diffStyle);
            }

            configureSheet(sheet, orderedNames.size());
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot create schema comparison Excel", exception);
        }
    }

    private void writeHeader(Sheet sheet, CellStyle style) {
        Row row = sheet.createRow(0);
        row.setHeightInPoints(32);
        for (int index = 0; index < HEADERS.length; index++) {
            setCell(row, index, HEADERS[index], style);
        }
    }

    private void writeDocumentSide(
            Row row,
            TableDefinition table,
            ColumnDefinition column,
            int usageCount,
            CellStyle style) {

        if (column == null) {
            fillEmpty(row, 0, 9, style);
            return;
        }

        setCell(row, 0, usageCount, style);
        setCell(row, 1, table.columns().indexOf(column) + 1, style);
        setCell(row, 2, column.name(), style);
        setCell(row, 3, column.description(), style);
        setCell(row, 4, format(column.dataType()), style);
        setCell(row, 5, documentKey(table, column), style);
        setCell(row, 6, column.unique() ? "Y" : "N", style);
        setCell(row, 7, column.indexed() ? "Y" : "N", style);
        setCell(row, 8, column.nullable() ? "N" : "Y", style);
        setCell(row, 9, column.defaultValue(), style);
    }

    private void writeDatabaseSide(Row row, Table table, Column column, CellStyle style) {
        if (column == null) {
            fillEmpty(row, 10, 18, style);
            return;
        }

        setCell(row, 10, column.ordinalPosition(), style);
        setCell(row, 11, column.name().value(), style);
        setCell(row, 12, format(column.dataType()), style);
        setCell(row, 13, column.nullable() ? "Y" : "N", style);
        setCell(row, 14, column.defaultValue().expression(), style);
        setCell(row, 15, column.description().value(), style);
        setCell(row, 16, indexNames(table, column.name(), false), style);
        setCell(row, 17, indexNames(table, column.name(), true), style);
        setCell(row, 18, foreignKeyNames(table, column.name()), style);
    }

    private List<String> compare(
            TableDefinition documentTable,
            ColumnDefinition documentColumn,
            Table databaseTable,
            Column databaseColumn) {

        List<String> differences = new ArrayList<>();
        if (documentColumn == null) {
            differences.add("NOT_EXISTS_IN_DOCUMENT");
            return differences;
        }
        if (databaseColumn == null) {
            differences.add("NOT_EXISTS_IN_DATABASE");
            return differences;
        }

        if (!normalizeExpression(format(documentColumn.dataType()))
                .equals(normalizeExpression(format(databaseColumn.dataType())))) {
            differences.add("DATA_TYPE");
        }
        if (documentColumn.nullable() != databaseColumn.nullable()) {
            differences.add("NULLABLE");
        }
        if (!normalizeExpression(documentColumn.defaultValue())
                .equals(normalizeExpression(databaseColumn.defaultValue().expression()))) {
            differences.add("DEFAULT");
        }
        if (!normalizeText(documentColumn.description())
                .equals(normalizeText(databaseColumn.description().value()))) {
            differences.add("COMMENTS");
        }
        if (documentColumn.indexed() != hasIndex(databaseTable, databaseColumn.name(), false)) {
            differences.add("INDEX");
        }
        boolean databaseUnique = hasIndex(databaseTable, databaseColumn.name(), true)
                || inUniqueKey(databaseTable, databaseColumn.name())
                || inPrimaryKey(databaseTable, databaseColumn.name());
        if (documentColumn.unique() != databaseUnique && !documentColumn.primaryKey()) {
            differences.add("UNIQUE");
        }
        if (documentColumn.primaryKey() != inPrimaryKey(databaseTable, databaseColumn.name())) {
            differences.add("PRIMARY_KEY");
        }
        if (documentHasForeignKey(documentTable, documentColumn.name())
                != hasForeignKey(databaseTable, databaseColumn.name())) {
            differences.add("FOREIGN_KEY");
        }
        return differences;
    }

    private String documentKey(TableDefinition table, ColumnDefinition column) {
        if (column.primaryKey()) {
            return "PK";
        }
        return table.foreignKeys().stream()
                .filter(foreignKey -> foreignKey.columns().stream()
                        .anyMatch(name -> normalize(name).equals(normalize(column.name()))))
                .map(foreignKey -> foreignKey.referencedTable())
                .findFirst()
                .orElse("");
    }

    private boolean documentHasForeignKey(TableDefinition table, String columnName) {
        return table.foreignKeys().stream()
                .flatMap(foreignKey -> foreignKey.columns().stream())
                .anyMatch(name -> normalize(name).equals(normalize(columnName)));
    }

    private boolean inPrimaryKey(Table table, Identifier column) {
        return table.primaryKey()
                .map(primaryKey -> primaryKey.columns().stream().anyMatch(column::equals))
                .orElse(false);
    }

    private boolean inUniqueKey(Table table, Identifier column) {
        return table.uniqueKeys().stream()
                .map(UniqueKey::columns)
                .flatMap(List::stream)
                .anyMatch(column::equals);
    }

    private boolean hasForeignKey(Table table, Identifier column) {
        return table.foreignKeys().stream()
                .map(ForeignKey::columns)
                .flatMap(List::stream)
                .anyMatch(column::equals);
    }

    private boolean hasIndex(Table table, Identifier column, boolean unique) {
        return table.indexes().stream()
                .filter(index -> index.type().name().contains("UNIQUE") == unique)
                .flatMap(index -> index.columns().stream())
                .map(IndexColumn::column)
                .anyMatch(column::equals);
    }

    private String indexNames(Table table, Identifier column, boolean unique) {
        return table.indexes().stream()
                .filter(index -> index.type().name().contains("UNIQUE") == unique)
                .filter(index -> index.columns().stream().map(IndexColumn::column).anyMatch(column::equals))
                .map(Index::name)
                .map(Identifier::value)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private String foreignKeyNames(Table table, Identifier column) {
        return table.foreignKeys().stream()
                .filter(foreignKey -> foreignKey.columns().contains(column))
                .map(foreignKey -> foreignKey.name() == null ? "" : foreignKey.name().value())
                .filter(value -> !value.isBlank())
                .sorted()
                .collect(Collectors.joining(","));
    }

    private List<String> orderedNames(TableDefinition documentTable, Table databaseTable) {
        Set<String> names = new LinkedHashSet<>();
        documentTable.columns().stream().map(ColumnDefinition::name).map(this::normalize).forEach(names::add);
        databaseTable.columns().stream()
                .sorted(Comparator.comparing(Column::ordinalPosition, Comparator.nullsLast(Integer::compareTo)))
                .map(Column::name)
                .map(Identifier::normalized)
                .forEach(names::add);
        return List.copyOf(names);
    }

    private int findUsageCount(Map<String, Integer> counts, String normalizedName) {
        return counts.entrySet().stream()
                .filter(entry -> normalize(entry.getKey()).equals(normalizedName))
                .map(Map.Entry::getValue)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(0);
    }

    private String format(DataTypeDefinition type) {
        String name = type.name().trim().toUpperCase(Locale.ROOT);
        if (type.length() != null) {
            return name + "(" + type.length() + ")";
        }
        if (type.precision() != null) {
            return type.scale() == null || type.scale() == 0
                    ? name + "(" + type.precision() + ")"
                    : name + "(" + type.precision() + "," + type.scale() + ")";
        }
        return name;
    }

    private String format(com.behsazan.schemaforge.domain.valueobject.DataType type) {
        String name = type.name().value().toUpperCase(Locale.ROOT);
        if (type.length() != null) {
            return name + "(" + type.length() + ")";
        }
        if (type.precision() != null) {
            return type.scale() == null || type.scale() == 0
                    ? name + "(" + type.precision() + ")"
                    : name + "(" + type.precision() + "," + type.scale() + ")";
        }
        return name;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeExpression(String value) {
        return normalize(value).replaceAll("\\s+", "");
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }

    private String safeSheetName(String value) {
        String candidate = value == null || value.isBlank() ? "Comparison" : value.trim();
        candidate = candidate.replaceAll("[\\\\/?*\\[\\]:]", "_");
        return candidate.substring(0, Math.min(candidate.length(), 31));
    }

    private void configureSheet(Sheet sheet, int rowCount) {
        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new CellRangeAddress(0, Math.max(1, rowCount), 0, HEADERS.length - 1));
        for (int index = 0; index < HEADERS.length; index++) {
            sheet.autoSizeColumn(index);
            sheet.setColumnWidth(index, Math.min(sheet.getColumnWidth(index) + 512, 15000));
        }
    }

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = createBaseStyle(workbook);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createNormalStyle(XSSFWorkbook workbook) {
        return createBaseStyle(workbook);
    }

    private CellStyle createDiffStyle(XSSFWorkbook workbook) {
        CellStyle style = createBaseStyle(workbook);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createBaseStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }

    private void fillEmpty(Row row, int from, int to, CellStyle style) {
        for (int index = from; index <= to; index++) {
            setCell(row, index, "", style);
        }
    }

    private void setCell(Row row, int index, Object value, CellStyle style) {
        Cell cell = row.createCell(index);
        if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else {
            cell.setCellValue(value == null ? "" : value.toString());
        }
        cell.setCellStyle(style);
    }
}
