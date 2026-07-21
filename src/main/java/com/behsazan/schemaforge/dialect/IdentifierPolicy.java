package com.behsazan.schemaforge.dialect;

public interface IdentifierPolicy {

    int maximumLength();

    String normalize(String identifier);

    String quote(String identifier);

    boolean isValidUnquoted(String identifier);
}
