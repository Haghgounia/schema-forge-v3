package com.behsazan.schemaforge.dialect.postgresql;

import com.behsazan.schemaforge.dialect.IdentifierPolicy;
import java.util.Locale;
import java.util.regex.Pattern;

public final class PostgreSqlIdentifierPolicy implements IdentifierPolicy {
    private static final Pattern UNQUOTED = Pattern.compile("[A-Za-z_][A-Za-z0-9_$]*");

    @Override public int maximumLength() { return 63; }
    @Override public String normalize(String identifier) {
        return identifier == null ? null : identifier.trim().toLowerCase(Locale.ROOT);
    }
    @Override public String quote(String identifier) {
        return identifier == null ? null : '"' + normalize(identifier).replace("\"", "\"\"") + '"';
    }
    @Override public boolean isValidUnquoted(String identifier) {
        return identifier != null && identifier.length() <= maximumLength() && UNQUOTED.matcher(identifier).matches();
    }
}
