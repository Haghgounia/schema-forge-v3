package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.domain.enums.ReferentialAction;
import com.behsazan.schemaforge.domain.model.CheckConstraint;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.ForeignKey;
import com.behsazan.schemaforge.domain.model.PrimaryKey;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.model.UniqueKey;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.generation.model.SqlSection;
import com.behsazan.schemaforge.generation.model.SqlStatement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generates Oracle table constraints without changing the canonical model.
 */
public final class OracleConstraintGenerator {

    public List<SqlSection> generate(DatabaseSchema schema) {
        List<SqlStatement> localConstraints = new ArrayList<>();
        List<SqlStatement> foreignKeys = new ArrayList<>();

        for (Table table : schema.tables().stream()
                .sorted(Comparator.comparing(item -> item.qualifiedName().toString()))
                .toList()) {

            table.primaryKey().ifPresent(primaryKey ->
                    localConstraints.add(statement(
                            buildPrimaryKey(table, primaryKey),
                            "PRIMARY_KEY",
                            table,
                            primaryKey.name(),
                            localConstraints.size()
                    ))
            );

            table.uniqueKeys().stream()
                    .sorted(Comparator.comparing(item -> item.name().value()))
                    .forEach(uniqueKey ->
                            localConstraints.add(statement(
                                    buildUniqueKey(table, uniqueKey),
                                    "UNIQUE_KEY",
                                    table,
                                    uniqueKey.name(),
                                    localConstraints.size()
                            ))
                    );

            table.checkConstraints().stream()
                    .sorted(Comparator.comparing(item -> item.name().value()))
                    .forEach(checkConstraint ->
                            localConstraints.add(statement(
                                    buildCheck(table, checkConstraint),
                                    "CHECK_CONSTRAINT",
                                    table,
                                    checkConstraint.name(),
                                    localConstraints.size()
                            ))
                    );

            table.foreignKeys().stream()
                    .sorted(Comparator.comparing(item -> item.name().value()))
                    .forEach(foreignKey ->
                            foreignKeys.add(statement(
                                    buildForeignKey(table, foreignKey),
                                    "FOREIGN_KEY",
                                    table,
                                    foreignKey.name(),
                                    foreignKeys.size()
                            ))
                    );
        }

        List<SqlSection> sections = new ArrayList<>();

        if (!localConstraints.isEmpty()) {
            sections.add(new SqlSection(
                    "Primary, Unique and Check Constraints",
                    120,
                    localConstraints
            ));
        }

        if (!foreignKeys.isEmpty()) {
            sections.add(new SqlSection(
                    "Foreign Keys",
                    140,
                    foreignKeys
            ));
        }

        return sections;
    }

    private String buildPrimaryKey(
            Table table,
            PrimaryKey primaryKey
    ) {
        return "ALTER TABLE "
                + table.qualifiedName()
                + " ADD CONSTRAINT "
                + primaryKey.name()
                + " PRIMARY KEY ("
                + columns(primaryKey.columns())
                + ");";
    }

    private String buildUniqueKey(
            Table table,
            UniqueKey uniqueKey
    ) {
        return "ALTER TABLE "
                + table.qualifiedName()
                + " ADD CONSTRAINT "
                + uniqueKey.name()
                + " UNIQUE ("
                + columns(uniqueKey.columns())
                + ");";
    }

    private String buildCheck(
            Table table,
            CheckConstraint checkConstraint
    ) {
        return "ALTER TABLE "
                + table.qualifiedName()
                + " ADD CONSTRAINT "
                + checkConstraint.name()
                + " CHECK ("
                + checkConstraint.expression()
                + ");";
    }

    private String buildForeignKey(
            Table table,
            ForeignKey foreignKey
    ) {
        StringBuilder sql = new StringBuilder();

        sql.append("ALTER TABLE ")
                .append(table.qualifiedName())
                .append(" ADD CONSTRAINT ")
                .append(foreignKey.name())
                .append(" FOREIGN KEY (")
                .append(columns(foreignKey.columns()))
                .append(")")
                .append(" REFERENCES ")
                .append(foreignKey.referencedTable())
                .append(" (")
                .append(columns(foreignKey.referencedColumns()))
                .append(")");

        String deleteClause = onDeleteClause(foreignKey.onDelete());

        if (!deleteClause.isEmpty()) {
            sql.append(" ")
                    .append(deleteClause);
        }

        sql.append(";");

        return sql.toString();
    }

    private String onDeleteClause(ReferentialAction action) {
        if (action == null) {
            return "";
        }

        return switch (action) {
            case CASCADE -> "ON DELETE CASCADE";
            case SET_NULL -> "ON DELETE SET NULL";
            default -> "";
        };
    }

    private String columns(List<Identifier> columns) {
        return columns.stream()
                .map(Identifier::value)
                .collect(Collectors.joining(", "));
    }

    private SqlStatement statement(
            String sql,
            String type,
            Table table,
            Identifier constraintName,
            int order
    ) {
        return new SqlStatement(
                sql,
                type,
                table.qualifiedName() + "." + constraintName,
                order
        );
    }
}