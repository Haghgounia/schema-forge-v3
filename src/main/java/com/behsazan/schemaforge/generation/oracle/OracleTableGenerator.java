package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.database.oracle.OracleDictionaryCache;
import com.behsazan.schemaforge.database.service.DatabaseDictionaryCache;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.generation.model.SqlDocument;
import com.behsazan.schemaforge.generation.model.SqlSection;
import com.behsazan.schemaforge.generation.model.SqlStatement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * @deprecated since 3.3. Replaced by {@link com.behsazan.schemaforge.generation.ddl.generator.table.TableDdlGenerator} or the vendor-neutral DDL pipeline.
 * Scheduled for removal in Phase 3.6.
 */
@Deprecated(forRemoval = true, since = "3.3")
public final class OracleTableGenerator {
    private final OracleCanonicalTypeMapper typeMapper;
    private final OracleColumnMetadataInspector metadataInspector;

    public OracleTableGenerator() {
        this(new OracleCanonicalTypeMapper(), null);
    }

    /** @deprecated Use the DBMS-neutral DatabaseDictionaryCache constructor. */
    @Deprecated(forRemoval = true)
    public OracleTableGenerator(OracleDictionaryCache dictionaryCache) {
        this(new OracleCanonicalTypeMapper(), dictionaryCache == null ? null : dictionaryCache.delegate());
    }

    public OracleTableGenerator(DatabaseDictionaryCache dictionaryCache) {
        this(new OracleCanonicalTypeMapper(), dictionaryCache);
    }

    public OracleTableGenerator(OracleCanonicalTypeMapper typeMapper) {
        this(typeMapper, null);
    }

    public OracleTableGenerator(
            OracleCanonicalTypeMapper typeMapper,
            DatabaseDictionaryCache dictionaryCache) {
        this.typeMapper = typeMapper;
        this.metadataInspector = new OracleColumnMetadataInspector(dictionaryCache, typeMapper);
    }

    public SqlDocument generate(DatabaseSchema schema, boolean includeComments) {
        List<SqlStatement> tables = new ArrayList<>();
        List<SqlStatement> comments = new ArrayList<>();
        int order = 0;
        for (Table table : schema.tables().stream()
                .sorted(Comparator.comparing(t -> t.qualifiedName().toString()))
                .toList()) {
            tables.add(new SqlStatement(buildTable(table), "TABLE", table.qualifiedName().toString(), order++));
            if (includeComments) {
                addComments(table, comments);
            }
        }
        List<SqlSection> sections = new ArrayList<>();
        sections.add(new SqlSection("Tables", 100, tables));
        if (!comments.isEmpty()) {
            sections.add(new SqlSection("Comments", 200, comments));
        }
        return new SqlDocument(sections);
    }

    private String buildTable(Table table) {
        StringBuilder sql = new StringBuilder("CREATE TABLE ")
                .append(table.qualifiedName()).append("\n(\n");
        List<Column> columns = table.columns().stream()
                .sorted(Comparator.comparing(c -> c.ordinalPosition() == null
                        ? Integer.MAX_VALUE : c.ordinalPosition()))
                .toList();
        for (int i = 0; i < columns.size(); i++) {
            appendMetadataWarnings(sql, columns.get(i));
            sql.append("    ").append(buildColumn(columns.get(i)));
            if (i < columns.size() - 1) {
                sql.append(',');
            }
            sql.append('\n');
        }
        sql.append(")");
        String tablespace = table.physicalOptions().get("tablespace");
        if (tablespace != null && !tablespace.isBlank()) {
            sql.append(" TABLESPACE ").append(tablespace.trim());
        }
        sql.append(';');
        return sql.toString();
    }

    private void appendMetadataWarnings(StringBuilder sql, Column column) {
        if (metadataInspector.isReservedWord(column)) {
            sql.append("    -- ERROR: Column ")
                    .append(column.name().value())
                    .append(" is an Oracle reserved word.\n");
        }

        OracleColumnMetadataInspector.DataTypeMismatch mismatch =
                metadataInspector.dataTypeMismatch(column);
        if (mismatch != null) {
            sql.append("    -- WARNING: Column ")
                    .append(column.name().value())
                    .append(" uses ")
                    .append(mismatch.documentType())
                    .append(" in the document; Oracle metadata standard is ")
                    .append(mismatch.databaseType())
                    .append(" across ")
                    .append(mismatch.databaseUsageCount())
                    .append(" table(s).\n");
        }
    }

    private String buildColumn(Column column) {
        int usageCount = metadataInspector.usageCount(column);
        StringBuilder sql = new StringBuilder(String.format(Locale.ROOT, "/*%4d*/  ", usageCount))
                .append(column.name().value())
                .append(' ')
                .append(typeMapper.map(column.dataType()));
        if (column.defaultValue().isPresent()) {
            sql.append(" DEFAULT ").append(column.defaultValue().expression());
        }
        if (!column.nullable()) {
            sql.append(" NOT NULL");
        }
        return sql.toString();
    }

    private void addComments(Table table, List<SqlStatement> comments) {
        if (!table.description().isEmpty()) {
            comments.add(new SqlStatement(
                    "COMMENT ON TABLE " + table.qualifiedName() + " IS '" + escape(table.description().value()) + "';",
                    "TABLE_COMMENT", table.qualifiedName().toString(), comments.size()));
        }
        for (Column column : table.columns()) {
            if (!column.description().isEmpty()) {
                comments.add(new SqlStatement(
                        "COMMENT ON COLUMN " + table.qualifiedName() + "." + column.name() + " IS '"
                                + escape(column.description().value()) + "';",
                        "COLUMN_COMMENT", table.qualifiedName() + "." + column.name(), comments.size()));
            }
        }
    }

    private String escape(String value) {
        return value.replace("'", "''");
    }
}
