package com.behsazan.schemaforge.validation.spelling;

import java.util.List;

/** Disabled spell-check implementation. */
public final class NoOpSpellCheckService implements SpellCheckService {
    @Override
    public List<SpellingError> check(String text) {
        return List.of();
    }
}
