package com.behsazan.schemaforge.database.oracle;

import com.behsazan.schemaforge.database.domain.ColumnState;
import com.behsazan.schemaforge.database.domain.ConstraintState;
import com.behsazan.schemaforge.database.domain.IndexState;
import com.behsazan.schemaforge.domain.enums.IndexType;
import com.behsazan.schemaforge.domain.enums.SortDirection;
import com.behsazan.schemaforge.domain.model.CheckConstraint;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.ForeignKey;
import com.behsazan.schemaforge.domain.model.Index;
import com.behsazan.schemaforge.domain.model.IndexColumn;
import com.behsazan.schemaforge.domain.model.PrimaryKey;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.model.UniqueKey;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.DefaultValue;
import com.behsazan.schemaforge.domain.valueobject.Description;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.domain.valueobject.QualifiedName;
import com.behsazan.schemaforge.domain.enums.ReferentialAction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class OracleCanonicalSchemaMapper {

    public Table mapTable(
            String owner,
            String tableName,
            String tableComment,
            List<ColumnState> columns,
            List<ConstraintState> constraints,
            List<IndexState> indexes) {

        Table.Builder builder = Table.builder(normalize(owner), normalize(tableName));
        if (tableComment != null && !tableComment.isBlank()) builder.description(tableComment);

        columns.stream()
                .sorted(Comparator.comparing(ColumnState::columnId, Comparator.nullsLast(Integer::compareTo)))
                .map(this::mapColumn)
                .forEach(builder::addColumn);

        mapConstraints(builder, constraints);
        mapIndexes(builder, indexes);
        return builder.build();
    }

    private Column mapColumn(ColumnState state) {
        return new Column(
                Identifier.of(state.name()),
                mapDataType(state),
                state.nullable(),
                new DefaultValue(state.defaultValue()),
                new Description(state.comment()),
                false,
                state.columnId());
    }

    private DataType mapDataType(ColumnState state) {
        String type = normalize(state.rawDataType());
        return switch (type) {
            case "CHAR", "VARCHAR", "VARCHAR2", "NCHAR", "NVARCHAR2", "RAW" ->
                    DataType.varchar(type, preferredLength(state));
            case "NUMBER", "DECIMAL", "NUMERIC" ->
                    state.precision() == null
                            ? DataType.simple(type)
                            : DataType.numeric(type, state.precision(), state.scale());
            default -> DataType.simple(canonicalTypeName(type));
        };
    }

    private String canonicalTypeName(String oracleType) {
        String canonical = oracleType.replaceAll("[^A-Z0-9_$#]+", "_");
        canonical = canonical.replaceAll("_+", "_").replaceAll("^_+|_+$", "");
        if (canonical.isEmpty() || !Character.isLetter(canonical.charAt(0))) {
            return "ORACLE_" + canonical;
        }
        return canonical;
    }

    private int preferredLength(ColumnState state) {
        Integer length = "C".equalsIgnoreCase(state.charUsed()) ? state.charLength() : state.length();
        if (length == null || length <= 0) length = state.charLength();
        if (length == null || length <= 0) length = 1;
        return length;
    }

    private void mapConstraints(Table.Builder builder, List<ConstraintState> states) {
        Map<String, List<ConstraintState>> groups = groupConstraints(states);
        for (List<ConstraintState> group : groups.values()) {
            ConstraintState first = group.getFirst();
            List<Identifier> columns = group.stream()
                    .sorted(Comparator.comparing(ConstraintState::columnPosition, Comparator.nullsLast(Integer::compareTo)))
                    .map(ConstraintState::columnName)
                    .filter(value -> value != null && !value.isBlank())
                    .map(Identifier::of)
                    .toList();
            Identifier name = identifierOrNull(first.name());
            switch (normalize(first.type())) {
                case "P" -> builder.primaryKey(new PrimaryKey(name, columns));
                case "U" -> builder.addUniqueKey(new UniqueKey(name, columns));
                case "C" -> {
                    if (first.expression() != null && !first.expression().isBlank()) {
                        builder.addCheck(new CheckConstraint(name, first.expression()));
                    }
                }
                case "R" -> {
                    List<Identifier> referencedColumns = group.stream()
                            .sorted(Comparator.comparing(ConstraintState::columnPosition, Comparator.nullsLast(Integer::compareTo)))
                            .map(ConstraintState::referencedColumn)
                            .filter(value -> value != null && !value.isBlank())
                            .map(Identifier::of)
                            .toList();
                    if (!columns.isEmpty() && columns.size() == referencedColumns.size()
                            && first.referencedTable() != null && !first.referencedTable().isBlank()) {
                        builder.addForeignKey(new ForeignKey(
                                name,
                                columns,
                                QualifiedName.of(first.referencedOwner(), first.referencedTable()),
                                referencedColumns,
                                ReferentialAction.NO_ACTION,
                                ReferentialAction.NO_ACTION));
                    }
                }
                default -> { }
            }
        }
    }

    private Map<String, List<ConstraintState>> groupConstraints(List<ConstraintState> states) {
        Map<String, List<ConstraintState>> groups = new LinkedHashMap<>();
        for (ConstraintState state : states == null ? List.<ConstraintState>of() : states) {
            String key = (state.name() == null ? "" : state.name()) + "|" + state.type();
            groups.computeIfAbsent(key, ignored -> new ArrayList<>()).add(state);
        }
        return groups;
    }

    private void mapIndexes(Table.Builder builder, List<IndexState> states) {
        Map<String, List<IndexState>> groups = new LinkedHashMap<>();
        for (IndexState state : states == null ? List.<IndexState>of() : states) {
            groups.computeIfAbsent(state.name(), ignored -> new ArrayList<>()).add(state);
        }
        for (Map.Entry<String, List<IndexState>> entry : groups.entrySet()) {
            List<IndexState> group = entry.getValue();
            List<IndexColumn> columns = group.stream()
                    .sorted(Comparator.comparing(IndexState::columnPosition, Comparator.nullsLast(Integer::compareTo)))
                    .filter(state -> state.columnName() != null && !state.columnName().isBlank())
                    .map(state -> new IndexColumn(
                            Identifier.of(state.columnName()),
                            "DESC".equalsIgnoreCase(state.descend()) ? SortDirection.DESC : SortDirection.ASC))
                    .toList();
            if (!columns.isEmpty()) {
                builder.addIndex(new Index(
                        Identifier.of(entry.getKey()),
                        columns,
                        group.getFirst().unique() ? IndexType.UNIQUE : IndexType.NORMAL,
                        Description.empty()));
            }
        }
    }

    public com.behsazan.schemaforge.domain.model.Sequence mapSequence(String owner, com.behsazan.schemaforge.database.domain.SequenceState state) {
        return new com.behsazan.schemaforge.domain.model.Sequence(
                QualifiedName.of(owner, state.name()),
                state.lastNumber(),
                state.incrementBy(),
                state.minValue(),
                state.maxValue(),
                state.cycle(),
                state.cacheSize(),
                Description.empty());
    }

    public com.behsazan.schemaforge.domain.model.View mapView(String owner, com.behsazan.schemaforge.database.domain.ViewState state) {
        return new com.behsazan.schemaforge.domain.model.View(
                QualifiedName.of(owner, state.name()),
                state.query(),
                Description.empty(),
                state.materialized());
    }

    public com.behsazan.schemaforge.domain.model.Synonym mapSynonym(String owner, com.behsazan.schemaforge.database.domain.SynonymState state) {
        String synonymOwner = state.publicSynonym() ? "PUBLIC" : owner;
        return new com.behsazan.schemaforge.domain.model.Synonym(
                QualifiedName.of(synonymOwner, state.name()),
                QualifiedName.of(state.targetOwner(), state.targetName()),
                state.publicSynonym(),
                Description.empty());
    }

    public com.behsazan.schemaforge.domain.model.Trigger mapTrigger(com.behsazan.schemaforge.database.domain.TriggerState state) {
        return new com.behsazan.schemaforge.domain.model.Trigger(
                QualifiedName.of(state.tableOwner(), state.name()),
                QualifiedName.of(state.tableOwner(), state.tableName()),
                state.timing(),
                state.event(),
                state.body(),
                Description.empty());
    }

    public com.behsazan.schemaforge.domain.model.Routine mapRoutine(String owner, com.behsazan.schemaforge.database.domain.RoutineState state) {
        com.behsazan.schemaforge.domain.enums.RoutineType type = "FUNCTION".equalsIgnoreCase(state.type())
                ? com.behsazan.schemaforge.domain.enums.RoutineType.FUNCTION
                : com.behsazan.schemaforge.domain.enums.RoutineType.PROCEDURE;
        DataType returnType = type == com.behsazan.schemaforge.domain.enums.RoutineType.FUNCTION
                ? DataType.simple("UNKNOWN")
                : null;
        return new com.behsazan.schemaforge.domain.model.Routine(
                QualifiedName.of(owner, state.name()),
                type,
                List.of(),
                returnType,
                state.body(),
                Description.empty());
    }

    private Identifier identifierOrNull(String value) {
        return value == null || value.isBlank() ? null : Identifier.of(value);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Oracle metadata value must not be blank");
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
