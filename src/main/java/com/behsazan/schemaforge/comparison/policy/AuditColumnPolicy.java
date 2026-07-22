package com.behsazan.schemaforge.comparison.policy;

import java.util.Set;

public final class AuditColumnPolicy {

    private static final Set<String> AUDIT_COLUMNS = Set.of(
            "CREATED_BY",
            "CREATED_DATE",
            "LAST_MODIFIED_BY",
            "LAST_MODIFIED_DATE"
    );


    private AuditColumnPolicy() {
    }


    public static boolean isAuditColumn(String columnName) {

        return columnName != null
                && AUDIT_COLUMNS.contains(
                columnName.toUpperCase()
        );
    }
}