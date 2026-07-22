package com.behsazan.schemaforge.validation.spelling;

import java.util.List;

/** A spelling finding returned by a spell-check provider. */
public record SpellingError(
        String word,
        String message,
        List<SpellingSuggestion> suggestions) {

    public SpellingError {
        word = word == null ? "" : word.trim();
        message = message == null ? "" : message.trim();
        suggestions = suggestions == null ? List.of() : List.copyOf(suggestions);
    }
}
