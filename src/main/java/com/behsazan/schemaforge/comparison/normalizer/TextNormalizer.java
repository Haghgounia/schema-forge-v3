package com.behsazan.schemaforge.comparison.normalizer;

import java.util.Locale;

public final class TextNormalizer {
    private TextNormalizer() { }

    public static String normalize(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }
}
