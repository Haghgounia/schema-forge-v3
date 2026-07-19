package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.generation.model.SqlDocument;
import com.behsazan.schemaforge.generation.model.SqlSection;
import com.behsazan.schemaforge.generation.model.SqlStatement;

public final class OracleSqlRenderer {
    public String render(SqlDocument document) {
        StringBuilder sql = new StringBuilder("SET DEFINE OFF;\nWHENEVER SQLERROR EXIT SQL.SQLCODE;\n\n");
        for (SqlSection section : document.sections()) {
            if (section.statements().isEmpty()) continue;
            sql.append("PROMPT ============================================================\n")
                    .append("PROMPT ").append(section.name()).append("\n")
                    .append("PROMPT ============================================================\n\n");
            for (SqlStatement statement : section.statements()) {
                sql.append(statement.sql()).append("\n\n");
            }
        }
        sql.append("PROMPT Schema generation completed successfully.\n");
        return sql.toString();
    }
}
