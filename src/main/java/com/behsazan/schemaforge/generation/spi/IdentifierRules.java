package com.behsazan.schemaforge.generation.spi;

import java.util.List;

public interface IdentifierRules {
    int maximumLength();
    boolean isReservedWord(String identifier);
    List<GenerationMessage> validate(String identifier, String objectType);
}
