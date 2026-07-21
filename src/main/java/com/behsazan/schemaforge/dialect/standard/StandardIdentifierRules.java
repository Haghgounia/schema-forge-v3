package com.behsazan.schemaforge.dialect.standard;

import com.behsazan.schemaforge.dialect.IdentifierRules;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class StandardIdentifierRules implements IdentifierRules {

    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");
    private static final Set<String> RESERVED = Set.of(
            "ALTER", "CREATE", "DELETE", "DROP", "FROM", "INSERT", "SELECT", "TABLE", "UPDATE", "WHERE");

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
