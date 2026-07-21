package com.behsazan.schemaforge.dialect;

import java.util.Set;

public interface IdentifierRules {

    int maxIdentifierLength();

    boolean isValidUnquotedIdentifier(String identifier);

    boolean isReservedWord(String identifier);

    Set<String> reservedWords();

    default String normalize(String identifier) {
        return identifier == null ? null : identifier.trim().toUpperCase();
    }
}
