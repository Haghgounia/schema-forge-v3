package com.behsazan.schemaforge.validation.oracle;

import java.util.Locale;
import java.util.Set;

/** Validates that an unquoted Oracle identifier is not a reserved word. */
public final class OracleReservedWordValidator {

    private static final Set<String> RESERVED_WORDS = Set.of(
            "ACCESS", "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "AUDIT",
            "BETWEEN", "BY", "CHAR", "CHECK", "CLUSTER", "COLUMN", "COMMENT", "COMPRESS",
            "CONNECT", "CREATE", "CURRENT", "DATE", "DECIMAL", "DEFAULT", "DELETE", "DESC",
            "DISTINCT", "DROP", "ELSE", "EXCLUSIVE", "EXISTS", "FILE", "FLOAT", "FOR", "FROM",
            "GRANT", "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE", "IN", "INCREMENT", "INDEX",
            "INITIAL", "INSERT", "INTEGER", "INTERSECT", "INTO", "IS", "LEVEL", "LIKE", "LOCK",
            "LONG", "MAXEXTENTS", "MINUS", "MLSLABEL", "MODE", "MODIFY", "NOAUDIT", "NOCOMPRESS",
            "NOT", "NOWAIT", "NULL", "NUMBER", "OF", "OFFLINE", "ON", "ONLINE", "OPTION", "OR",
            "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES", "PUBLIC", "RAW", "RENAME", "RESOURCE",
            "REVOKE", "ROW", "ROWID", "ROWNUM", "ROWS", "SELECT", "SESSION", "SET", "SHARE",
            "SIZE", "SMALLINT", "START", "SUCCESSFUL", "SYNONYM", "SYSDATE", "TABLE", "THEN",
            "TO", "TRIGGER", "UID", "UNION", "UNIQUE", "UPDATE", "USER", "VALIDATE", "VALUES",
            "VARCHAR", "VARCHAR2", "VIEW", "WHENEVER", "WHERE", "WITH"
    );

    public void requireNotReserved(String value, String objectType) {
        if (value == null || value.isBlank()) {
            return;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (RESERVED_WORDS.contains(normalized)) {
            throw new IllegalArgumentException(
                    "Oracle " + normalizedObjectType(objectType)
                            + " identifier '" + value.trim() + "' is a reserved word"
            );
        }
    }

    public boolean isReserved(String value) {
        return value != null
                && RESERVED_WORDS.contains(value.trim().toUpperCase(Locale.ROOT));
    }

    private String normalizedObjectType(String objectType) {
        return objectType == null || objectType.isBlank() ? "object" : objectType.trim();
    }
}
