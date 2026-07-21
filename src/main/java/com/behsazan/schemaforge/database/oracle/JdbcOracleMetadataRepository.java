package com.behsazan.schemaforge.database.oracle;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import com.behsazan.schemaforge.database.domain.ColumnState;
import com.behsazan.schemaforge.database.domain.ConstraintState;
import com.behsazan.schemaforge.database.domain.IndexState;
import com.behsazan.schemaforge.database.domain.RoutineState;
import com.behsazan.schemaforge.database.domain.SequenceState;
import com.behsazan.schemaforge.database.domain.SynonymState;
import com.behsazan.schemaforge.database.domain.TriggerState;
import com.behsazan.schemaforge.database.domain.ViewState;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Deprecated(forRemoval = true)
public class JdbcOracleMetadataRepository implements OracleMetadataRepository {

    private static final String SCHEMA_EXISTS_SQL = "SELECT COUNT(*) FROM ALL_USERS WHERE USERNAME = :schema";
    private static final String TABLESPACE_EXISTS_SQL = "SELECT COUNT(*) FROM DBA_TABLESPACES WHERE TABLESPACE_NAME = :tablespace";
    private static final String TABLE_EXISTS_SQL = "SELECT COUNT(*) FROM ALL_TABLES WHERE OWNER = :owner AND TABLE_NAME = :tableName";
    private static final String TABLE_NAMES_SQL = "SELECT TABLE_NAME FROM ALL_TABLES WHERE OWNER = :owner ORDER BY TABLE_NAME";
    private static final String TABLE_COMMENT_SQL = "SELECT COMMENTS FROM ALL_TAB_COMMENTS WHERE OWNER = :owner AND TABLE_NAME = :tableName AND TABLE_TYPE = 'TABLE'";
    private static final String COLUMNS_SQL = """
            SELECT c.COLUMN_ID, c.COLUMN_NAME, c.DATA_TYPE, c.DATA_LENGTH, c.CHAR_LENGTH,
                   c.CHAR_USED, c.DATA_PRECISION, c.DATA_SCALE, c.NULLABLE, c.DATA_DEFAULT,
                   cc.COMMENTS
              FROM ALL_TAB_COLUMNS c
              LEFT JOIN ALL_COL_COMMENTS cc
                ON cc.OWNER = c.OWNER AND cc.TABLE_NAME = c.TABLE_NAME AND cc.COLUMN_NAME = c.COLUMN_NAME
             WHERE c.OWNER = :owner AND c.TABLE_NAME = :tableName
             ORDER BY c.COLUMN_ID
            """;
    private static final String CONSTRAINTS_SQL = """
            SELECT c.CONSTRAINT_NAME, c.CONSTRAINT_TYPE, cc.COLUMN_NAME,
                   cc.POSITION AS COLUMN_POSITION, c.SEARCH_CONDITION_VC AS EXPRESSION,
                   rc.OWNER AS REFERENCED_OWNER, rc.TABLE_NAME AS REFERENCED_TABLE,
                   rcc.COLUMN_NAME AS REFERENCED_COLUMN
              FROM ALL_CONSTRAINTS c
              LEFT JOIN ALL_CONS_COLUMNS cc
                ON cc.OWNER = c.OWNER AND cc.CONSTRAINT_NAME = c.CONSTRAINT_NAME AND cc.TABLE_NAME = c.TABLE_NAME
              LEFT JOIN ALL_CONSTRAINTS rc
                ON rc.OWNER = c.R_OWNER AND rc.CONSTRAINT_NAME = c.R_CONSTRAINT_NAME
              LEFT JOIN ALL_CONS_COLUMNS rcc
                ON rcc.OWNER = rc.OWNER AND rcc.CONSTRAINT_NAME = rc.CONSTRAINT_NAME
               AND rcc.TABLE_NAME = rc.TABLE_NAME AND rcc.POSITION = cc.POSITION
             WHERE c.OWNER = :owner AND c.TABLE_NAME = :tableName
               AND c.CONSTRAINT_TYPE IN ('P', 'R', 'U', 'C')
               AND (c.CONSTRAINT_TYPE <> 'C' OR c.GENERATED = 'USER NAME')
             ORDER BY c.CONSTRAINT_NAME, cc.POSITION
            """;
    private static final String INDEXES_SQL = """
            SELECT i.INDEX_NAME, i.UNIQUENESS, ic.COLUMN_NAME, ic.COLUMN_POSITION, ic.DESCEND
              FROM ALL_INDEXES i
              JOIN ALL_IND_COLUMNS ic
                ON ic.INDEX_OWNER = i.OWNER AND ic.INDEX_NAME = i.INDEX_NAME
               AND ic.TABLE_OWNER = i.TABLE_OWNER AND ic.TABLE_NAME = i.TABLE_NAME
             WHERE i.TABLE_OWNER = :owner AND i.TABLE_NAME = :tableName
             ORDER BY i.INDEX_NAME, ic.COLUMN_POSITION
            """;
    private static final String SEQUENCES_SQL = """
            SELECT SEQUENCE_NAME, MIN_VALUE, MAX_VALUE, INCREMENT_BY, CYCLE_FLAG, CACHE_SIZE, LAST_NUMBER
              FROM ALL_SEQUENCES
             WHERE SEQUENCE_OWNER = :owner
             ORDER BY SEQUENCE_NAME
            """;
    private static final String VIEWS_SQL = """
            SELECT VIEW_NAME AS OBJECT_NAME, TEXT AS QUERY_TEXT
              FROM ALL_VIEWS
             WHERE OWNER = :owner
             ORDER BY VIEW_NAME
            """;
    private static final String MATERIALIZED_VIEWS_SQL = """
            SELECT MVIEW_NAME AS OBJECT_NAME, QUERY AS QUERY_TEXT
              FROM ALL_MVIEWS
             WHERE OWNER = :owner
             ORDER BY MVIEW_NAME
            """;
    private static final String SYNONYMS_SQL = """
            SELECT OWNER, SYNONYM_NAME, TABLE_OWNER, TABLE_NAME
              FROM ALL_SYNONYMS
             WHERE OWNER IN (:owner, 'PUBLIC')
               AND TABLE_OWNER = :owner
             ORDER BY OWNER, SYNONYM_NAME
            """;
    private static final String TRIGGERS_SQL = """
            SELECT TRIGGER_NAME, TABLE_OWNER, TABLE_NAME, TRIGGERING_EVENT, TRIGGER_TYPE, TRIGGER_BODY
              FROM ALL_TRIGGERS
             WHERE OWNER = :owner
               AND BASE_OBJECT_TYPE = 'TABLE'
             ORDER BY TRIGGER_NAME
            """;
    private static final String ROUTINES_SQL = """
            SELECT OBJECT_NAME, OBJECT_TYPE
              FROM ALL_PROCEDURES
             WHERE OWNER = :owner
               AND OBJECT_TYPE IN ('PROCEDURE', 'FUNCTION')
               AND PROCEDURE_NAME IS NULL
             ORDER BY OBJECT_TYPE, OBJECT_NAME
            """;
    private static final String ROUTINE_SOURCE_SQL = """
            SELECT TEXT
              FROM ALL_SOURCE
             WHERE OWNER = :owner
               AND NAME = :name
               AND TYPE = :type
             ORDER BY LINE
            """;
    private static final String RESERVED_WORDS_SQL = "SELECT DISTINCT UPPER(TRIM(KEYWORD)) AS KEYWORD FROM V$RESERVED_WORDS WHERE KEYWORD IS NOT NULL";
    private static final String COLUMN_USAGE_COUNTS_SQL = """
            SELECT c.COLUMN_NAME, COUNT(DISTINCT c.OWNER || '.' || c.TABLE_NAME) AS USAGE_COUNT
              FROM ALL_TAB_COLUMNS c JOIN ALL_USERS u ON u.USERNAME = c.OWNER
             WHERE u.ORACLE_MAINTAINED = 'N' GROUP BY c.COLUMN_NAME
            """;
    private static final String COLUMN_DATA_TYPE_USAGE_SQL = """
            SELECT c.COLUMN_NAME, c.DATA_TYPE, c.DATA_LENGTH, c.DATA_PRECISION, c.DATA_SCALE, COUNT(*) AS USAGE_COUNT
              FROM ALL_TAB_COLUMNS c JOIN ALL_USERS u ON u.USERNAME = c.OWNER
             WHERE u.ORACLE_MAINTAINED = 'N'
             GROUP BY c.COLUMN_NAME, c.DATA_TYPE, c.DATA_LENGTH, c.DATA_PRECISION, c.DATA_SCALE
             ORDER BY c.COLUMN_NAME, USAGE_COUNT DESC
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final OracleCanonicalSchemaMapper mapper = new OracleCanonicalSchemaMapper();

    public JdbcOracleMetadataRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override public DatabaseType databaseType() { return DatabaseType.ORACLE; }

    @Override
    public DatabaseSchema inspectSchema(String schemaName) {
        String owner = normalize(schemaName);
        if (!schemaExists(owner)) throw new IllegalArgumentException("Oracle schema does not exist: " + owner);
        DatabaseSchema.Builder schema = DatabaseSchema.builder(owner).metadata("source", "ORACLE_METADATA");
        for (String tableName : findTableNames(owner)) {
            schema.addTable(mapper.mapTable(owner, tableName, findTableComment(owner, tableName),
                    findColumns(owner, tableName), findConstraints(owner, tableName), findIndexes(owner, tableName)));
        }
        findSequences(owner).stream().map(state -> mapper.mapSequence(owner, state)).forEach(schema::addSequence);
        findViews(owner).stream().map(state -> mapper.mapView(owner, state)).forEach(schema::addView);
        findMaterializedViews(owner).stream().map(state -> mapper.mapView(owner, state)).forEach(schema::addView);
        findSynonyms(owner).stream().map(state -> mapper.mapSynonym(owner, state)).forEach(schema::addSynonym);
        findTriggers(owner).stream().map(mapper::mapTrigger).forEach(schema::addTrigger);
        findStandaloneRoutines(owner).stream().map(state -> mapper.mapRoutine(owner, state)).forEach(schema::addRoutine);
        return schema.build();
    }

    @Override public boolean schemaExists(String schema) { return count(SCHEMA_EXISTS_SQL, Map.of("schema", normalize(schema))) > 0; }
    @Override public boolean tablespaceExists(String tablespace) { return count(TABLESPACE_EXISTS_SQL, Map.of("tablespace", normalize(tablespace))) > 0; }
    @Override public boolean tableExists(String owner, String tableName) { return count(TABLE_EXISTS_SQL, parameters(owner, tableName)) > 0; }

    @Override
    public List<String> findTableNames(String owner) {
        return jdbcTemplate.query(TABLE_NAMES_SQL, Map.of("owner", normalize(owner)),
                (rs, rowNum) -> rs.getString("TABLE_NAME"));
    }

    @Override
    public String findTableComment(String owner, String tableName) {
        List<String> comments = jdbcTemplate.query(TABLE_COMMENT_SQL, parameters(owner, tableName),
                (rs, rowNum) -> trimToNull(rs.getString("COMMENTS")));
        return comments.isEmpty() ? null : comments.getFirst();
    }

    @Override
    public List<ColumnState> findColumns(String owner, String tableName) {
        return jdbcTemplate.query(COLUMNS_SQL, parameters(owner, tableName), (rs, rowNum) -> new ColumnState(
                nullableInteger(rs, "COLUMN_ID"), rs.getString("COLUMN_NAME"), rs.getString("DATA_TYPE"),
                nullableInteger(rs, "DATA_LENGTH"), nullableInteger(rs, "CHAR_LENGTH"), rs.getString("CHAR_USED"),
                nullableInteger(rs, "DATA_PRECISION"), nullableInteger(rs, "DATA_SCALE"),
                "Y".equalsIgnoreCase(rs.getString("NULLABLE")), trimToNull(rs.getString("DATA_DEFAULT")),
                trimToNull(rs.getString("COMMENTS"))));
    }

    @Override
    public List<ConstraintState> findConstraints(String owner, String tableName) {
        return jdbcTemplate.query(CONSTRAINTS_SQL, parameters(owner, tableName), (rs, rowNum) -> new ConstraintState(
                rs.getString("CONSTRAINT_NAME"), rs.getString("CONSTRAINT_TYPE"), rs.getString("COLUMN_NAME"),
                nullableInteger(rs, "COLUMN_POSITION"), trimToNull(rs.getString("EXPRESSION")),
                rs.getString("REFERENCED_OWNER"), rs.getString("REFERENCED_TABLE"), rs.getString("REFERENCED_COLUMN")));
    }

    @Override
    public List<IndexState> findIndexes(String owner, String tableName) {
        return jdbcTemplate.query(INDEXES_SQL, parameters(owner, tableName), (rs, rowNum) -> new IndexState(
                rs.getString("INDEX_NAME"), "UNIQUE".equalsIgnoreCase(rs.getString("UNIQUENESS")),
                rs.getString("COLUMN_NAME"), nullableInteger(rs, "COLUMN_POSITION"), rs.getString("DESCEND")));
    }


    @Override
    public List<SequenceState> findSequences(String owner) {
        return jdbcTemplate.query(SEQUENCES_SQL, Map.of("owner", normalize(owner)), (rs, rowNum) -> new SequenceState(
                rs.getString("SEQUENCE_NAME"), rs.getLong("MIN_VALUE"), rs.getLong("MAX_VALUE"),
                rs.getLong("INCREMENT_BY"), "Y".equalsIgnoreCase(rs.getString("CYCLE_FLAG")),
                rs.getInt("CACHE_SIZE"), rs.getLong("LAST_NUMBER")));
    }

    @Override
    public List<ViewState> findViews(String owner) {
        return findViews(owner, VIEWS_SQL, false);
    }

    @Override
    public List<ViewState> findMaterializedViews(String owner) {
        return findViews(owner, MATERIALIZED_VIEWS_SQL, true);
    }

    private List<ViewState> findViews(String owner, String sql, boolean materialized) {
        return jdbcTemplate.query(sql, Map.of("owner", normalize(owner)), (rs, rowNum) -> new ViewState(
                rs.getString("OBJECT_NAME"), defaultIfBlank(rs.getString("QUERY_TEXT"), "SELECT 1 FROM DUAL"), materialized));
    }

    @Override
    public List<SynonymState> findSynonyms(String owner) {
        return jdbcTemplate.query(SYNONYMS_SQL, Map.of("owner", normalize(owner)), (rs, rowNum) -> new SynonymState(
                rs.getString("SYNONYM_NAME"), rs.getString("TABLE_OWNER"), rs.getString("TABLE_NAME"),
                "PUBLIC".equalsIgnoreCase(rs.getString("OWNER"))));
    }

    @Override
    public List<TriggerState> findTriggers(String owner) {
        return jdbcTemplate.query(TRIGGERS_SQL, Map.of("owner", normalize(owner)), (rs, rowNum) -> new TriggerState(
                rs.getString("TRIGGER_NAME"), rs.getString("TABLE_OWNER"), rs.getString("TABLE_NAME"),
                rs.getString("TRIGGER_TYPE"), rs.getString("TRIGGERING_EVENT"),
                defaultIfBlank(rs.getString("TRIGGER_BODY"), "BEGIN NULL; END;")));
    }

    @Override
    public List<RoutineState> findStandaloneRoutines(String owner) {
        String normalizedOwner = normalize(owner);
        return jdbcTemplate.query(ROUTINES_SQL, Map.of("owner", normalizedOwner), (rs, rowNum) -> {
            String name = rs.getString("OBJECT_NAME");
            String type = rs.getString("OBJECT_TYPE");
            List<String> lines = jdbcTemplate.query(
                    ROUTINE_SOURCE_SQL,
                    Map.of("owner", normalizedOwner, "name", name, "type", type),
                    (sourceRs, sourceRow) -> sourceRs.getString("TEXT"));
            String body = lines.stream().filter(java.util.Objects::nonNull).collect(java.util.stream.Collectors.joining());
            return new RoutineState(name, type, defaultIfBlank(body, "BEGIN NULL; END;"));
        });
    }

    @Override
    public Set<String> loadReservedWords() {
        return jdbcTemplate.query(RESERVED_WORDS_SQL, Map.of(), rs -> {
            Set<String> result = new HashSet<>();
            while (rs.next()) { String value = rs.getString("KEYWORD"); if (value != null && !value.isBlank()) result.add(normalize(value)); }
            return Set.copyOf(result);
        });
    }

    @Override
    public Map<String, Integer> loadColumnUsageCounts() {
        Map<String, Integer> result = new HashMap<>();
        jdbcTemplate.query(COLUMN_USAGE_COUNTS_SQL, rs -> {
            while (rs.next()) { String name = rs.getString("COLUMN_NAME"); if (name != null) result.put(normalize(name), rs.getInt("USAGE_COUNT")); }
            return null;
        });
        return Map.copyOf(result);
    }

    @Override
    public Map<String, List<ColumnDataTypeUsage>> loadColumnDataTypeUsages() {
        Map<String, List<ColumnDataTypeUsage>> result = new HashMap<>();
        jdbcTemplate.query(COLUMN_DATA_TYPE_USAGE_SQL, rs -> {
            while (rs.next()) {
                String name = normalize(rs.getString("COLUMN_NAME"));
                result.computeIfAbsent(name, ignored -> new ArrayList<>()).add(new ColumnDataTypeUsage(
                        name, rs.getString("DATA_TYPE"), nullableInteger(rs, "DATA_LENGTH"),
                        nullableInteger(rs, "DATA_PRECISION"), nullableInteger(rs, "DATA_SCALE"), rs.getInt("USAGE_COUNT")));
            }
            return null;
        });
        result.replaceAll((key, value) -> List.copyOf(value));
        return Map.copyOf(result);
    }

    private int count(String sql, Map<String, ?> params) {
        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count == null ? 0 : count;
    }
    private Map<String, String> parameters(String owner, String tableName) { return Map.of("owner", normalize(owner), "tableName", normalize(tableName)); }
    private Integer nullableInteger(ResultSet rs, String column) throws SQLException { int value = rs.getInt(column); return rs.wasNull() ? null : value; }
    private String trimToNull(String value) { if (value == null) return null; String trimmed = value.trim(); return trimmed.isEmpty() ? null : trimmed; }
    private String defaultIfBlank(String value, String fallback) { String trimmed = trimToNull(value); return trimmed == null ? fallback : trimmed; }
    private String normalize(String value) { if (value == null || value.isBlank()) throw new IllegalArgumentException("Oracle object name must not be blank"); return value.trim().toUpperCase(Locale.ROOT); }
}
