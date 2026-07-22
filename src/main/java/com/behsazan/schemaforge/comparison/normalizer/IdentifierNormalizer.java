package com.behsazan.schemaforge.comparison.normalizer;

import com.behsazan.schemaforge.domain.valueobject.Identifier;
import java.util.Locale;

public final class IdentifierNormalizer {
    private IdentifierNormalizer() { }

    public static String normalize(Identifier identifier) {
        return identifier == null ? "" : identifier.normalized();
    }

    public static String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }
}
