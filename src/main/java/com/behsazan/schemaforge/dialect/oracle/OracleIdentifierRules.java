package com.behsazan.schemaforge.dialect.oracle;

import com.behsazan.schemaforge.dialect.IdentifierRules;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class OracleIdentifierRules implements IdentifierRules {

    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z][A-Za-z0-9_$#]*");
    private static final Set<String> RESERVED = Set.of(
            "ACCESS", "ADD", "ALTER", "AND", "AS", "ASC", "AUDIT", "BETWEEN", "BY", "CHAR", "CHECK",
            "CLUSTER", "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "CREATE", "CURRENT", "DATE", "DECIMAL",
            "DEFAULT", "DELETE", "DESC", "DISTINCT", "DROP", "ELSE", "EXCLUSIVE", "EXISTS", "FILE", "FLOAT",
            "FOR", "FROM", "GRANT", "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE", "IN", "INCREMENT", "INDEX",
            "INITIAL", "INSERT", "INTEGER", "INTERSECT", "INTO", "IS", "LEVEL", "LIKE", "LOCK", "LONG", "MAXEXTENTS",
            "MINUS", "MLSLABEL", "MODE", "MODIFY", "NOAUDIT", "NOCOMPRESS", "NOT", "NOWAIT", "NULL", "NUMBER",
            "OF", "OFFLINE", "ON", "ONLINE", "OPTION", "OR", "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES", "PUBLIC",
            "RAW", "RENAME", "RESOURCE", "REVOKE", "ROW", "ROWID", "ROWNUM", "ROWS", "SELECT", "SESSION", "SET",
            "SHARE", "SIZE", "SMALLINT", "START", "SUCCESSFUL", "SYNONYM", "SYSDATE", "TABLE", "THEN", "TO", "TRIGGER",
            "UID", "UNION", "UNIQUE", "UPDATE", "USER", "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2", "VIEW", "WHENEVER", "WHERE", "WITH");

    @Override
    public int maxIdentifierLength() {
        return 128;
    }

    @Override
    public boolean isValidUnquotedIdentifier(String identifier) {
        return identifier != null
                && identifier.length() <= maxIdentifierLength()
                && IDENTIFIER.matcher(identifier).matches();
    }

    @Override
    public boolean isReservedWord(String identifier) {
        return identifier != null && RESERVED.contains(identifier.trim().toUpperCase(Locale.ROOT));
    }

    @Override
    public Set<String> reservedWords() {
        return RESERVED;
    }
}
