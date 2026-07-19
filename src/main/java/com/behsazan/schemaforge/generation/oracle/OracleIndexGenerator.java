package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.domain.enums.IndexType;
import com.behsazan.schemaforge.domain.enums.SortDirection;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Index;
import com.behsazan.schemaforge.domain.model.IndexColumn;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.generation.model.SqlSection;
import com.behsazan.schemaforge.generation.model.SqlStatement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Generates standalone Oracle indexes after table constraints. */
public final class OracleIndexGenerator {

    public SqlSection generate(DatabaseSchema schema) {
        List<SqlStatement> statements = new ArrayList<>();
        for (Table table : schema.tables().stream()
                .sorted(Comparator.comparing(item -> item.qualifiedName().toString()))
                .toList()) {
            table.indexes().stream()
                    .sorted(Comparator.comparing(item -> item.name().value()))
                    .forEach(index -> statements.add(new SqlStatement(
                            buildIndex(table, index), "INDEX", qualifiedIndexName(table, index), statements.size())));
        }
        return new SqlSection("Indexes", 160, statements);
    }

    private String buildIndex(Table table, Index index) {
        StringBuilder sql = new StringBuilder("CREATE ");

        if (index.type() == IndexType.UNIQUE) {
            sql.append("UNIQUE ");
        } else if (index.type() == IndexType.BITMAP) {
            sql.append("BITMAP ");
        }

        sql.append("INDEX ")
                .append(qualifiedIndexName(table, index))
                .append(" ON ")
                .append(table.qualifiedName())
                .append(" (")
                .append(columns(index))
                .append(')');

        return sql.append(';').toString();
    }

    private String qualifiedIndexName(Table table, Index index) {
        return table.qualifiedName().schema() + "." + index.name();
    }

    private String columns(Index index) {
        return index.columns().stream().map(this::column).collect(Collectors.joining(", "));
    }

    private String column(IndexColumn column) {
        return column.column().value() + (column.direction() == SortDirection.DESC ? " DESC" : "");
    }
}
