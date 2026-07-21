package com.behsazan.schemaforge.dialect.standard;

import com.behsazan.schemaforge.dialect.ReservedWordProvider;

import java.util.Locale;
import java.util.Set;

public final class StandardReservedWordProvider implements ReservedWordProvider {

    private static final Set<String> WORDS = Set.of(
            "ALTER", "AND", "AS", "BY", "CHECK", "CREATE", "DELETE", "DROP", "FROM",
            "GROUP", "INSERT", "INTO", "NOT", "NULL", "OR", "ORDER", "SELECT", "TABLE",
            "UNIQUE", "UPDATE", "VALUES", "VIEW", "WHERE"
    );

    @Override
    public boolean isReserved(String word) {
        return word != null && WORDS.contains(word.trim().toUpperCase(Locale.ROOT));
    }

    @Override
    public Set<String> words() {
        return WORDS;
    }
}
