package com.behsazan.schemaforge.specification.adapter.docx;

import com.behsazan.schemaforge.domain.enums.IndexType;
import com.behsazan.schemaforge.domain.enums.ReferentialAction;
import com.behsazan.schemaforge.domain.enums.SortDirection;
import com.behsazan.schemaforge.domain.model.CheckConstraint;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.ForeignKey;
import com.behsazan.schemaforge.domain.model.Index;
import com.behsazan.schemaforge.domain.model.IndexColumn;
import com.behsazan.schemaforge.domain.model.PrimaryKey;
import com.behsazan.schemaforge.domain.model.Sequence;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.model.UniqueKey;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.DefaultValue;
import com.behsazan.schemaforge.domain.valueobject.Description;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.domain.valueobject.QualifiedName;
import com.behsazan.schemaforge.specification.spi.SpecificationParser;
import com.behsazan.schemaforge.specification.spi.SpecificationSource;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parses the established SchemaForge table-design DOCX format into the canonical model. */
@Component
public final class DocxSpecificationParser implements SpecificationParser {
    private static final Pattern DATA_TYPE = Pattern.compile(
            "(?i)([A-Z][A-Z0-9_ ]*)(?:\\s*\\(\\s*(\\d+)\\s*(?:,\\s*(\\d+)\\s*)?\\))?");
    private static final Pattern GROUP_REFERENCE = Pattern.compile("(?i)^([A-Z][A-Z0-9_$.]*)(?:/([YN]))?$" );
    private static final Pattern GROUP_POSITION = Pattern.compile("(?i)^([A-Z]+\\d+)(?:\\s*[,;:]\\s*(\\d+))?$" );

    @Override
    public boolean supports(String fileName) {
        return fileName != null && fileName.toLowerCase(Locale.ROOT).endsWith(".docx");
    }

    @Override
    public DatabaseSchema parse(SpecificationSource source) {
        Objects.requireNonNull(source, "source must not be null");
        try (XWPFDocument document = new XWPFDocument(source.content())) {
            Metadata metadata = readMetadata(document);
            List<ParsedColumn> parsedColumns = readColumns(document);
            if (parsedColumns.isEmpty()) {
                throw new IllegalArgumentException("No column definitions were found in " + source.fileName());
            }

            String schemaName = requireIdentifier(metadata.schema(), "schema", source.fileName());
            String tableName = requireIdentifier(metadata.tableName(), "table", source.fileName());
            Table table = buildTable(schemaName, tableName, metadata.description(), parsedColumns);

            DatabaseSchema.Builder schema = DatabaseSchema.builder(schemaName)
                    .metadata("source.fileName", source.fileName())
                    .addTable(table);

            if (parsedColumns.stream().anyMatch(ParsedColumn::identity)) {
                String sequenceName = "SEQ_" + tableName;
                schema.addSequence(new Sequence(
                        QualifiedName.of(schemaName, sequenceName),
                        1,
                        1,
                        null,
                        null,
                        false,
                        null,
                        Description.empty()));
            }
            return schema.build();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Unable to read DOCX specification: " + source.fileName(), exception);
        }
    }

    private Table buildTable(String schemaName, String tableName, String description, List<ParsedColumn> parsedColumns) {
        Table.Builder table = Table.builder(schemaName, tableName).description(description);
        String sequenceExpression = schemaName + ".SEQ_" + tableName + ".NEXTVAL";

        for (int index = 0; index < parsedColumns.size(); index++) {
            ParsedColumn parsed = parsedColumns.get(index);
            String defaultExpression = parsed.identity() ? sequenceExpression : emptyToNull(parsed.defaultValue());
            table.addColumn(new Column(
                    Identifier.of(parsed.name()),
                    parsed.dataType(),
                    !parsed.required(),
                    new DefaultValue(defaultExpression),
                    new Description(parsed.description()),
                    parsed.identity(),
                    index + 1));
        }

        List<String> primaryKeyColumns = parsedColumns.stream()
                .filter(ParsedColumn::primaryKey)
                .map(ParsedColumn::name)
                .toList();
        if (!primaryKeyColumns.isEmpty()) {
            table.primaryKey(new PrimaryKey(
                    Identifier.of("PK_" + tableName),
                    identifiers(primaryKeyColumns)));
        }

        addUniqueKeys(table, tableName, parsedColumns);
        addIndexes(table, tableName, parsedColumns);
        addForeignKeys(table, tableName, parsedColumns);
        addChecks(table, tableName, parsedColumns);
        return table.build();
    }

    private void addUniqueKeys(Table.Builder table, String tableName, List<ParsedColumn> columns) {
        Map<String, List<PositionedColumn>> groups = groupColumns(columns, ParsedColumn::uniqueToken);
        groups.forEach((group, members) -> table.addUniqueKey(new UniqueKey(
                Identifier.of(normalizeObjectName("UK", tableName, group)),
                identifiers(sortedNames(members)))));
    }

    private void addIndexes(Table.Builder table, String tableName, List<ParsedColumn> columns) {
        Map<String, List<PositionedColumn>> groups = groupColumns(columns, ParsedColumn::indexToken);
        groups.forEach((group, members) -> table.addIndex(new Index(
                Identifier.of(normalizeObjectName("IX", tableName, group)),
                sortedNames(members).stream()
                        .map(name -> new IndexColumn(Identifier.of(name), SortDirection.ASC))
                        .toList(),
                IndexType.NORMAL,
                Description.empty())));
    }

    private void addForeignKeys(Table.Builder table, String tableName, List<ParsedColumn> columns) {
        for (ParsedColumn column : columns) {
            if (column.referenceTable() == null) {
                continue;
            }
            Reference reference = parseReference(column.referenceTable());
            table.addForeignKey(new ForeignKey(
                    Identifier.of("FK_" + tableName + "_" + column.name()),
                    List.of(Identifier.of(column.name())),
                    QualifiedName.of(reference.schema(), reference.table()),
                    List.of(Identifier.of(column.name())),
                    ReferentialAction.NO_ACTION,
                    ReferentialAction.NO_ACTION));
        }
    }

    private void addChecks(Table.Builder table, String tableName, List<ParsedColumn> columns) {
        for (ParsedColumn column : columns) {
            String expression = firstNonBlank(column.checkConstraint(), column.range());
            if (expression == null) {
                continue;
            }
            table.addCheck(new CheckConstraint(
                    Identifier.of("CK_" + tableName + "_" + column.name()),
                    qualifyCheckExpression(column.name(), expression)));
        }
    }

    private String qualifyCheckExpression(String columnName, String expression) {
        String normalized = normalizeText(expression);
        if (normalized.matches("^(>=|<=|<>|!=|=|>|<).*")) {
            return columnName + " " + normalized;
        }
        if (normalized.matches("^\\d+\\s*\\.\\.\\s*\\d+$")) {
            String[] bounds = normalized.split("\\.\\.");
            return columnName + " BETWEEN " + bounds[0].trim() + " AND " + bounds[1].trim();
        }
        return normalized;
    }

    private Map<String, List<PositionedColumn>> groupColumns(
            List<ParsedColumn> columns,
            java.util.function.Function<ParsedColumn, String> tokenExtractor) {
        Map<String, List<PositionedColumn>> groups = new LinkedHashMap<>();
        for (ParsedColumn column : columns) {
            String token = emptyToNull(tokenExtractor.apply(column));
            if (token == null) {
                continue;
            }
            Matcher matcher = GROUP_POSITION.matcher(normalizeText(token).replace(" ", ""));
            String group = matcher.matches() ? matcher.group(1) : token.replaceAll("[^A-Za-z0-9_$#]", "_");
            int position = matcher.matches() && matcher.group(2) != null
                    ? Integer.parseInt(matcher.group(2))
                    : Integer.MAX_VALUE;
            groups.computeIfAbsent(group.toUpperCase(Locale.ROOT), ignored -> new ArrayList<>())
                    .add(new PositionedColumn(column.name(), position));
        }
        return groups;
    }

    private List<String> sortedNames(List<PositionedColumn> columns) {
        return columns.stream()
                .sorted(Comparator.comparingInt(PositionedColumn::position))
                .map(PositionedColumn::name)
                .toList();
    }

    private String normalizeObjectName(String prefix, String tableName, String group) {
        String normalized = group.toUpperCase(Locale.ROOT);
        if (normalized.startsWith(prefix)) {
            return normalized;
        }
        return prefix + "_" + tableName + "_" + normalized;
    }

    private Reference parseReference(String rawReference) {
        String normalized = normalizeText(rawReference).replace(" ", "").toUpperCase(Locale.ROOT);
        Matcher matcher = GROUP_REFERENCE.matcher(normalized);
        String object = matcher.matches() ? matcher.group(1) : normalized.split("/")[0];
        int dot = object.lastIndexOf('.');
        return dot < 0
                ? new Reference(null, object)
                : new Reference(object.substring(0, dot), object.substring(dot + 1));
    }

    private Metadata readMetadata(XWPFDocument document) {
        for (XWPFTable table : document.getTables()) {
            if (table.getNumberOfRows() < 2) {
                continue;
            }
            Map<Header, Integer> headers = mapHeaders(table.getRow(0));
            if (!headers.containsKey(Header.TABLE_NAME) || !headers.containsKey(Header.SCHEMA)) {
                continue;
            }
            XWPFTableRow values = table.getRow(1);
            return new Metadata(
                    cell(values, headers.get(Header.TABLE_NAME)),
                    cell(values, headers.get(Header.SCHEMA)),
                    cell(values, headers.get(Header.TABLE_DESCRIPTION)));
        }
        throw new IllegalArgumentException("Table metadata section was not found in the DOCX specification");
    }

    private List<ParsedColumn> readColumns(XWPFDocument document) {
        XWPFTable table = document.getTables().stream()
                .filter(candidate -> {
                    if (candidate.getNumberOfRows() == 0) {
                        return false;
                    }
                    Map<Header, Integer> headers = mapHeaders(candidate.getRow(0));
                    return headers.containsKey(Header.COLUMN_NAME) && headers.containsKey(Header.DATA_TYPE);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Column specification table was not found"));

        Map<Header, Integer> headers = mapHeaders(table.getRow(0));
        List<ParsedColumn> result = new ArrayList<>();
        for (int rowIndex = 1; rowIndex < table.getNumberOfRows(); rowIndex++) {
            XWPFTableRow row = table.getRow(rowIndex);
            String name = normalizeIdentifier(cell(row, headers.get(Header.COLUMN_NAME)));
            if (name == null) {
                continue;
            }
            String rawType = cell(row, headers.get(Header.DATA_TYPE));
            if (rawType == null || rawType.isBlank()) {
                continue;
            }
            String key = cell(row, headers.get(Header.KEY));
            result.add(new ParsedColumn(
                    name,
                    cell(row, headers.get(Header.COLUMN_DESCRIPTION)),
                    parseDataType(rawType),
                    normalizeText(rawType).toUpperCase(Locale.ROOT).contains("IDENTITY"),
                    containsToken(key, "PK"),
                    extractReference(key),
                    isMarked(cell(row, headers.get(Header.REQUIRED))),
                    cell(row, headers.get(Header.DEFAULT_VALUE)),
                    cell(row, headers.get(Header.UNIQUE)),
                    cell(row, headers.get(Header.INDEX)),
                    cell(row, headers.get(Header.RANGE)),
                    cell(row, headers.get(Header.CHECK_CONSTRAINT))));
        }
        return result;
    }

    private DataType parseDataType(String rawValue) {
        String normalized = normalizeText(rawValue)
                .toUpperCase(Locale.ROOT)
                .replace("IDENTITY", "")
                .replaceAll("\\s+", " ")
                .trim();
        Matcher matcher = DATA_TYPE.matcher(normalized);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Unsupported data type: " + rawValue);
        }
        String name = matcher.group(1).trim().replace(" ", "");
        Integer first = matcher.group(2) == null ? null : Integer.valueOf(matcher.group(2));
        Integer second = matcher.group(3) == null ? null : Integer.valueOf(matcher.group(3));
        if (first == null) {
            return DataType.simple(name);
        }
        if (isLengthType(name)) {
            return DataType.varchar(name, first);
        }
        return DataType.numeric(name, first, second);
    }

    private boolean isLengthType(String name) {
        return name.equals("VARCHAR2") || name.equals("VARCHAR") || name.equals("CHAR")
                || name.equals("NVARCHAR2") || name.equals("NCHAR") || name.equals("RAW");
    }

    private Map<Header, Integer> mapHeaders(XWPFTableRow row) {
        Map<Header, Integer> result = new EnumMap<>(Header.class);
        if (row == null) {
            return result;
        }
        for (int index = 0; index < row.getTableCells().size(); index++) {
            Header header = Header.from(cell(row, index));
            if (header != Header.UNKNOWN) {
                result.putIfAbsent(header, index);
            }
        }
        return result;
    }

    private String extractReference(String keyValue) {
        String value = emptyToNull(keyValue);
        if (value == null || containsToken(value, "PK") || !value.contains("/")) {
            return null;
        }
        return value;
    }

    private boolean containsToken(String value, String token) {
        return value != null && normalizeText(value).toUpperCase(Locale.ROOT).contains(token);
    }

    private boolean isMarked(String value) {
        if (value == null) {
            return false;
        }
        String normalized = normalizeText(value).toUpperCase(Locale.ROOT);
        return normalized.contains("√") || normalized.equals("Y") || normalized.equals("YES")
                || normalized.equals("TRUE") || normalized.equals("1");
    }

    private String cell(XWPFTableRow row, Integer index) {
        if (row == null || index == null || index < 0 || index >= row.getTableCells().size()) {
            return null;
        }
        XWPFTableCell cell = row.getCell(index);
        return cell == null ? null : emptyToNull(cell.getText());
    }


    private String normalizeIdentifier(String value) {
        String normalized = emptyToNull(value);
        if (normalized == null) {
            return null;
        }

        normalized = normalized
                .replace('\u00A0', ' ')   // Non-breaking space
                .replace('\u2007', ' ')   // Figure space
                .replace('\u202F', ' ')   // Narrow no-break space
                .replace("\uFEFF", "")    // BOM
                .replace("\u200B", "")    // Zero width space
                .replace("\u200C", "")    // Zero width non-joiner
                .replace("\u200D", "")    // Zero width joiner
                .replace('\t', ' ')
                .replace('\r', ' ')
                .replace('\n', ' ');

        normalized = normalized
                .trim()
                .replaceAll("\\s+", "");

        return normalized.toUpperCase(Locale.ROOT);
    }

    private String requireIdentifier(String value, String type, String fileName) {
        String normalized = normalizeIdentifier(value);
        if (normalized == null) {
            throw new IllegalArgumentException("Missing " + type + " name in " + fileName);
        }
        Identifier.of(normalized);
        return normalized;
    }

    private List<Identifier> identifiers(List<String> names) {
        return names.stream().map(Identifier::of).toList();
    }

    private String firstNonBlank(String first, String second) {
        String value = emptyToNull(first);
        return value != null ? value : emptyToNull(second);
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }

        value = value
                .replace('\u00A0', ' ')
                .replace('\u2007', ' ')
                .replace('\u202F', ' ')
                .replace("\uFEFF", "")
                .replace("\u200B", "")
                .replace("\u200C", "")
                .replace("\u200D", "")
                .replace('\t', ' ')
                .replace('\r', ' ')
                .replace('\n', ' ')
                .trim();

        return value.isEmpty() ? null : value;
    }

    private String normalizeText(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace('\u00A0', ' ')
                .replace('\u2007', ' ')
                .replace('\u202F', ' ')
                .replace("\uFEFF", "")
                .replace("\u200B", "")
                .replace("\u200C", "")
                .replace("\u200D", "")
                .replace('\t', ' ')
                .replace('\r', ' ')
                .replace('\n', ' ')
                .trim()
                .replaceAll("\\s+", " ");
    }

    private enum Header {
        TABLE_NAME,
        SCHEMA,
        TABLE_DESCRIPTION,
        COLUMN_NAME,
        COLUMN_DESCRIPTION,
        DATA_TYPE,
        KEY,
        UNIQUE,
        INDEX,
        REQUIRED,
        DEFAULT_VALUE,
        RANGE,
        CHECK_CONSTRAINT,
        UNKNOWN;

        private static Header from(String rawValue) {
            String value = rawValue == null ? "" : rawValue.replace('\n', ' ').replace('\r', ' ')
                                                   .trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
            if (value.contains("TABLE NAME")) return TABLE_NAME;
            if (value.equals("SCHEMA") || value.contains("SCHEMA ")) return SCHEMA;
            if (value.contains("هدف از طراحی جدول") || value.contains("TABLE PURPOSE")) return TABLE_DESCRIPTION;
            if (value.contains("COLUMN NAME")) return COLUMN_NAME;
            if (value.contains("نام فارسی ستون") || value.contains("PERSIAN COLUMN")) return COLUMN_DESCRIPTION;
            if (value.contains("DATA TYPE") || value.contains("نوع داده")) return DATA_TYPE;
            if (value.contains("PRIMARY") || value.contains("FOREIGN") || value.contains("کلید")) return KEY;
            if (value.contains("UNIQUE") || value.contains("یکتا")) return UNIQUE;
            if (value.equals("INDEX") || value.contains("شاخص")) return INDEX;
            if (value.contains("REQUIRED") || value.contains("اجباری")) return REQUIRED;
            if (value.contains("DEFAULT") || value.contains("پیش فرض")) return DEFAULT_VALUE;
            if (value.equals("RANGE") || value.contains("محدوده")) return RANGE;
            if (value.contains("CHECK") || value.contains("محدودیت کنترلی")) return CHECK_CONSTRAINT;
            return UNKNOWN;
        }
    }

    private record Metadata(String tableName, String schema, String description) {
    }

    private record ParsedColumn(
            String name,
            String description,
            DataType dataType,
            boolean identity,
            boolean primaryKey,
            String referenceTable,
            boolean required,
            String defaultValue,
            String uniqueToken,
            String indexToken,
            String range,
            String checkConstraint) {
    }

    private record PositionedColumn(String name, int position) {
    }

    private record Reference(String schema, String table) {
    }
}
