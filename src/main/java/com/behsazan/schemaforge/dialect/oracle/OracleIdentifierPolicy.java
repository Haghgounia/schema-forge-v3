package com.behsazan.schemaforge.dialect.oracle;

import com.behsazan.schemaforge.dialect.IdentifierPolicy;

import java.util.Locale;
import java.util.regex.Pattern;

public final class OracleIdentifierPolicy implements IdentifierPolicy {

    private static final Pattern UNQUOTED = Pattern.compile("[A-Za-z][A-Za-z0-9_$#]*");

    @Override
    public int maximumLength() {
        return 128;
    }

    @Override
    public String normalize(String identifier) {
        return identifier == null ? null : identifier.trim().toUpperCase(Locale.ROOT);
    }

    @Override
    public String quote(String identifier) {
        if (identifier == null) {
            return null;
        }
        return '"' + identifier.replace("\"", "\"\"") + '"';
    }

    @Override
    public boolean isValidUnquoted(String identifier) {
        return identifier != null
                && identifier.length() <= maximumLength()
                && UNQUOTED.matcher(identifier).matches();
    }
}
