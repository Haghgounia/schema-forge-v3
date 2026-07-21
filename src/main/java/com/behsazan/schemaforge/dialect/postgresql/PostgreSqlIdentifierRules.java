package com.behsazan.schemaforge.dialect.postgresql;

import com.behsazan.schemaforge.dialect.IdentifierRules;
import java.util.Set;

public final class PostgreSqlIdentifierRules implements IdentifierRules {
    private final PostgreSqlIdentifierPolicy policy = new PostgreSqlIdentifierPolicy();
    private final PostgreSqlReservedWordProvider reservedWords = new PostgreSqlReservedWordProvider();
    @Override public int maxIdentifierLength() { return policy.maximumLength(); }
    @Override public boolean isValidUnquotedIdentifier(String identifier) { return policy.isValidUnquoted(identifier); }
    @Override public boolean isReservedWord(String identifier) { return reservedWords.isReserved(identifier); }
    @Override public Set<String> reservedWords() { return reservedWords.words(); }
    @Override public String normalize(String identifier) { return policy.normalize(identifier); }
}
