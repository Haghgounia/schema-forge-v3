package com.behsazan.schemaforge.database.postgresql;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.DefaultValue;
import com.behsazan.schemaforge.domain.valueobject.Description;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(prefix = "schemaforge.postgresql", name = "enabled", havingValue = "true")
@ConditionalOnExpression("T(org.springframework.util.StringUtils).hasText(\'${schemaforge.postgresql.url:}\')")
public class JdbcPostgreSqlMetadataProvider implements PostgreSqlMetadataProvider {

    private static final String SCHEMA_EXISTS_SQL = """
            SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = :schema
            """;
    private static final String TABLES_SQL = """
            SELECT table_name
              FROM information_schema.tables
             WHERE table_schema = :schema AND table_type = 'BASE TABLE'
             ORDER BY table_name
            """;
    private static final String COLUMNS_SQL = """
            SELECT column_name, data_type, udt_name, character_maximum_length,
                   numeric_precision, numeric_scale, is_nullable, column_default,
                   ordinal_position, is_identity
              FROM information_schema.columns
             WHERE table_schema = :schema AND table_name = :table
             ORDER BY ordinal_position
            """;
    private static final String RESERVED_WORDS_SQL = """
            SELECT upper(word) AS keyword FROM pg_get_keywords() WHERE catcode IN ('R', 'T')
            """;
    private static final String COLUMN_USAGE_COUNTS_SQL = """
            SELECT upper(column_name) AS column_name,
                   COUNT(DISTINCT table_schema || '.' || table_name) AS usage_count
              FROM information_schema.columns
             WHERE table_schema NOT IN ('pg_catalog', 'information_schema')
             GROUP BY upper(column_name)
            """;
    private static final String COLUMN_DATA_TYPE_USAGE_SQL = """
            SELECT upper(column_name) AS column_name,
                   upper(CASE WHEN data_type = 'USER-DEFINED' THEN udt_name ELSE data_type END) AS data_type,
                   character_maximum_length AS data_length,
                   numeric_precision AS data_precision,
                   numeric_scale AS data_scale,
                   COUNT(*) AS usage_count
              FROM information_schema.columns
             WHERE table_schema NOT IN ('pg_catalog', 'information_schema')
             GROUP BY upper(column_name),
                      upper(CASE WHEN data_type = 'USER-DEFINED' THEN udt_name ELSE data_type END),
                      character_maximum_length, numeric_precision, numeric_scale
             ORDER BY column_name, usage_count DESC
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcPostgreSqlMetadataProvider(
            @Qualifier("postgresqlJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override public DatabaseType databaseType() { return DatabaseType.POSTGRESQL; }

    @Override
    public DatabaseSchema inspectSchema(String schemaName) {
        String schema = normalizeSchema(schemaName);
        Integer count = jdbcTemplate.queryForObject(SCHEMA_EXISTS_SQL, Map.of("schema", schema), Integer.class);
        if (count == null || count == 0) {
            throw new IllegalArgumentException("PostgreSQL schema does not exist: " + schema);
        }
        DatabaseSchema.Builder result = DatabaseSchema.builder(schema).metadata("source", "POSTGRESQL_METADATA");
        List<String> tables = jdbcTemplate.query(TABLES_SQL, Map.of("schema", schema),
                (rs, rowNum) -> rs.getString("table_name"));
        for (String tableName : tables) {
            Table.Builder table = Table.builder(schema, tableName);
            jdbcTemplate.query(COLUMNS_SQL, Map.of("schema", schema, "table", tableName), rs -> {
                while (rs.next()) table.addColumn(mapColumn(rs));
                return null;
            });
            result.addTable(table.build());
        }
        return result.build();
    }

    private Column mapColumn(ResultSet rs) throws SQLException {
        String typeName = rs.getString("data_type");
        if ("USER-DEFINED".equalsIgnoreCase(typeName)) typeName = rs.getString("udt_name");
        Integer length = nullableInteger(rs, "character_maximum_length");
        Integer precision = nullableInteger(rs, "numeric_precision");
        Integer scale = nullableInteger(rs, "numeric_scale");
        String canonicalType = canonicalTypeName(typeName);
        DataType dataType;
        if (length != null) dataType = DataType.varchar(canonicalType, length);
        else if (precision != null && isNumeric(typeName)) dataType = DataType.numeric(canonicalType, precision, scale);
        else dataType = DataType.simple(canonicalType);
        return new Column(
                Identifier.of(rs.getString("column_name")), dataType,
                "YES".equalsIgnoreCase(rs.getString("is_nullable")),
                new DefaultValue(rs.getString("column_default")), Description.empty(),
                "YES".equalsIgnoreCase(rs.getString("is_identity")),
                nullableInteger(rs, "ordinal_position"));
    }

    @Override
    public Set<String> loadReservedWords() {
        return jdbcTemplate.query(RESERVED_WORDS_SQL, Map.of(), rs -> {
            Set<String> result = new HashSet<>();
            while (rs.next()) result.add(normalizeIdentifier(rs.getString("keyword")));
            return Set.copyOf(result);
        });
    }

    @Override
    public Map<String, Integer> loadColumnUsageCounts() {
        Map<String, Integer> result = new HashMap<>();
        jdbcTemplate.query(COLUMN_USAGE_COUNTS_SQL, rs -> {
            while (rs.next()) result.put(normalizeIdentifier(rs.getString("column_name")), rs.getInt("usage_count"));
            return null;
        });
        return Map.copyOf(result);
    }

    @Override
    public Map<String, List<ColumnDataTypeUsage>> loadColumnDataTypeUsages() {
        Map<String, List<ColumnDataTypeUsage>> result = new HashMap<>();
        jdbcTemplate.query(COLUMN_DATA_TYPE_USAGE_SQL, rs -> {
            while (rs.next()) {
                String name = normalizeIdentifier(rs.getString("column_name"));
                result.computeIfAbsent(name, ignored -> new ArrayList<>()).add(new ColumnDataTypeUsage(
                        name, rs.getString("data_type"), nullableInteger(rs, "data_length"),
                        nullableInteger(rs, "data_precision"), nullableInteger(rs, "data_scale"),
                        rs.getInt("usage_count")));
            }
            return null;
        });
        result.replaceAll((key, value) -> List.copyOf(value));
        return Map.copyOf(result);
    }

    private static boolean isNumeric(String typeName) {
        String normalized = typeName.toUpperCase(Locale.ROOT);
        return normalized.equals("NUMERIC") || normalized.equals("DECIMAL");
    }
    private static String canonicalTypeName(String typeName) {
        String normalized = typeName == null ? "TEXT" : typeName.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "CHARACTER VARYING", "VARCHAR" -> "VARCHAR";
            case "CHARACTER", "CHAR" -> "CHAR";
            case "DOUBLE PRECISION" -> "DOUBLE_PRECISION";
            case "TIMESTAMP WITHOUT TIME ZONE" -> "TIMESTAMP";
            case "TIMESTAMP WITH TIME ZONE" -> "TIMESTAMPTZ";
            case "TIME WITHOUT TIME ZONE" -> "TIME";
            case "TIME WITH TIME ZONE" -> "TIMETZ";
            case "USER-DEFINED" -> "TEXT";
            default -> normalized.replace(' ', '_');
        };
    }
    private static Integer nullableInteger(ResultSet rs, String name) throws SQLException {
        int value = rs.getInt(name);
        return rs.wasNull() ? null : value;
    }
    private static String normalizeSchema(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("PostgreSQL schema name must not be blank");
        return value.trim().toLowerCase(Locale.ROOT);
    }
    private static String normalizeIdentifier(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("PostgreSQL identifier must not be blank");
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
