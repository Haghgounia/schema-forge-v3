package com.behsazan.schemaforge.reporting;

import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.ForeignKey;
import com.behsazan.schemaforge.domain.model.Index;
import com.behsazan.schemaforge.domain.model.IndexColumn;
import com.behsazan.schemaforge.domain.model.Sequence;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

/** Writes the extracted canonical schema as a reviewable Excel workbook. */
@Component
public final class SchemaExcelWriter {

    public byte[] write(DatabaseSchema schema) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            CellStyle header = headerStyle(workbook);
            writeSummary(workbook, schema, header);
            writeTables(workbook, schema, header);
            writeColumns(workbook, schema, header);
            writePrimaryKeys(workbook, schema, header);
            writeForeignKeys(workbook, schema, header);
            writeIndexes(workbook, schema, header);
            writeSequences(workbook, schema, header);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot create schema Excel", exception);
        }
    }

    private void writeSummary(XSSFWorkbook workbook, DatabaseSchema schema, CellStyle header) {
        Sheet sheet = workbook.createSheet("Summary");
        row(sheet, 0, header, "ITEM", "VALUE");
        row(sheet, 1, null, "Schema", schema.name().value());
        row(sheet, 2, null, "Tables", schema.tables().size());
        row(sheet, 3, null, "Columns", schema.tables().stream().mapToInt(t -> t.columns().size()).sum());
        row(sheet, 4, null, "Primary Keys", schema.tables().stream().filter(t -> t.primaryKey().isPresent()).count());
        row(sheet, 5, null, "Foreign Keys", schema.tables().stream().mapToInt(t -> t.foreignKeys().size()).sum());
        row(sheet, 6, null, "Indexes", schema.tables().stream().mapToInt(t -> t.indexes().size()).sum());
        row(sheet, 7, null, "Sequences", schema.sequences().size());
        finish(sheet, 2);
    }

    private void writeTables(XSSFWorkbook workbook, DatabaseSchema schema, CellStyle header) {
        Sheet sheet = workbook.createSheet("Tables");
        row(sheet, 0, header, "SCHEMA", "TABLE_NAME", "DESCRIPTION", "COLUMN_COUNT", "PK", "FK_COUNT", "INDEX_COUNT");
        int index = 1;
        for (Table table : schema.tables()) {
            row(sheet, index++, null,
                    table.qualifiedName().schema().value(), table.qualifiedName().name().value(),
                    table.description().value(), table.columns().size(),
                    table.primaryKey().map(pk -> pk.name().value()).orElse(""),
                    table.foreignKeys().size(), table.indexes().size());
        }
        finish(sheet, 7);
    }

    private void writeColumns(XSSFWorkbook workbook, DatabaseSchema schema, CellStyle header) {
        Sheet sheet = workbook.createSheet("Columns");
        row(sheet, 0, header, "SCHEMA", "TABLE_NAME", "ORDINAL", "COLUMN_NAME", "DATA_TYPE", "NULLABLE", "DEFAULT", "IDENTITY", "DESCRIPTION");
        int index = 1;
        for (Table table : schema.tables()) {
            for (Column column : table.columns()) {
                row(sheet, index++, null,
                        table.qualifiedName().schema().value(), table.qualifiedName().name().value(),
                        column.ordinalPosition(), column.name().value(), formatType(column),
                        column.nullable() ? "Y" : "N", column.defaultValue().expression(),
                        column.identity() ? "Y" : "N", column.description().value());
            }
        }
        finish(sheet, 9);
    }

    private void writePrimaryKeys(XSSFWorkbook workbook, DatabaseSchema schema, CellStyle header) {
        Sheet sheet = workbook.createSheet("PrimaryKeys");
        row(sheet, 0, header, "SCHEMA", "TABLE_NAME", "CONSTRAINT_NAME", "COLUMNS");
        int index = 1;
        for (Table table : schema.tables()) {
            if (table.primaryKey().isPresent()) {
                var key = table.primaryKey().orElseThrow();
                row(sheet, index++, null, table.qualifiedName().schema().value(),
                        table.qualifiedName().name().value(), key.name().value(), identifiers(key.columns()));
            }
        }
        finish(sheet, 4);
    }

    private void writeForeignKeys(XSSFWorkbook workbook, DatabaseSchema schema, CellStyle header) {
        Sheet sheet = workbook.createSheet("ForeignKeys");
        row(sheet, 0, header, "SCHEMA", "TABLE_NAME", "CONSTRAINT_NAME", "COLUMNS", "REFERENCED_TABLE", "REFERENCED_COLUMNS", "DELETE_RULE");
        int index = 1;
        for (Table table : schema.tables()) {
            for (ForeignKey key : table.foreignKeys()) {
                row(sheet, index++, null, table.qualifiedName().schema().value(),
                        table.qualifiedName().name().value(), key.name().value(), identifiers(key.columns()),
                        key.referencedTable().toString(), identifiers(key.referencedColumns()), key.onDelete().name());
            }
        }
        finish(sheet, 7);
    }

    private void writeIndexes(XSSFWorkbook workbook, DatabaseSchema schema, CellStyle header) {
        Sheet sheet = workbook.createSheet("Indexes");
        row(sheet, 0, header, "SCHEMA", "TABLE_NAME", "INDEX_NAME", "TYPE", "COLUMNS", "DESCRIPTION");
        int index = 1;
        for (Table table : schema.tables()) {
            for (Index item : table.indexes()) {
                String columns = item.columns().stream()
                        .map(this::formatIndexColumn)
                        .collect(Collectors.joining(","));
                row(sheet, index++, null, table.qualifiedName().schema().value(),
                        table.qualifiedName().name().value(), item.name().value(), item.type().name(),
                        columns, item.description().value());
            }
        }
        finish(sheet, 6);
    }

    private void writeSequences(XSSFWorkbook workbook, DatabaseSchema schema, CellStyle header) {
        Sheet sheet = workbook.createSheet("Sequences");
        row(sheet, 0, header, "SCHEMA", "SEQUENCE_NAME", "START_WITH", "INCREMENT_BY", "MIN_VALUE", "MAX_VALUE", "CYCLE", "CACHE");
        int index = 1;
        for (Sequence sequence : schema.sequences()) {
            row(sheet, index++, null, sequence.qualifiedName().schema().value(),
                    sequence.qualifiedName().name().value(), sequence.startWith(), sequence.incrementBy(),
                    sequence.minValue(), sequence.maxValue(), sequence.cycle() ? "Y" : "N", sequence.cacheSize());
        }
        finish(sheet, 8);
    }

    private String formatType(Column column) {
        var type = column.dataType();
        if (type.length() != null) {
            return type.name().value() + "(" + type.length() + ")";
        }
        if (type.precision() != null) {
            return type.scale() == null ? type.name().value() + "(" + type.precision() + ")"
                    : type.name().value() + "(" + type.precision() + "," + type.scale() + ")";
        }
        return type.name().value();
    }

    private String identifiers(List<Identifier> identifiers) {
        return identifiers.stream().map(Identifier::value).collect(Collectors.joining(","));
    }

    private String formatIndexColumn(IndexColumn column) {
        return column.column().value() + " " + column.direction().name();
    }

    private void row(Sheet sheet, int rowIndex, CellStyle style, Object... values) {
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            Object value = values[i];
            if (value instanceof Number number) {
                cell.setCellValue(number.doubleValue());
            } else {
                cell.setCellValue(value == null ? "" : String.valueOf(value));
            }
            if (style != null) {
                cell.setCellStyle(style);
            }
        }
    }

    private CellStyle headerStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void finish(Sheet sheet, int columns) {
        sheet.createFreezePane(0, 1);
        sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, Math.max(0, sheet.getLastRowNum()), 0, columns - 1));
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.min(sheet.getColumnWidth(i) + 512, 18000));
        }
    }
}
