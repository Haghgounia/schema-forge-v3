package com.behsazan.schemaforge.validation.spelling;

/** A single LanguageTool replacement suggestion. */
public record SpellingSuggestion(String value) {
    public SpellingSuggestion {
        value = value == null ? "" : value.trim();
    }
}
