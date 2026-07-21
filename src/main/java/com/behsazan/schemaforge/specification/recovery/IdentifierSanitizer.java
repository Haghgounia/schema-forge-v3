package com.behsazan.schemaforge.specification.recovery;

import java.util.Locale;

/** Best-effort sanitizer for unquoted database identifiers extracted from DOCX cells. */
public final class IdentifierSanitizer {
    public RecoveryResult sanitize(String rawValue, String objectType) {
        String original = rawValue == null ? "" : rawValue.trim();
        String normalized = original.toUpperCase(Locale.ROOT)
                .replaceAll("[\\s.\\-]+", "_")
                .replaceAll("[^A-Z0-9_$#]", "")
                .replaceAll("_+", "_")
                .replaceAll("^_+|_+$", "");

        if (normalized.isBlank()) {
            normalized = "RECOVERED_" + objectType.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "_");
        }
        if (!Character.isLetter(normalized.charAt(0))) {
            normalized = "X_" + normalized;
        }
        if (normalized.length() > 128) {
            normalized = normalized.substring(0, 128);
        }

        if (normalized.equals(original.toUpperCase(Locale.ROOT))) {
            return RecoveryResult.unchanged(normalized);
        }
        return RecoveryResult.recovered(normalized,
                "Sanitized " + objectType + " identifier '" + original + "' to '" + normalized + "'");
    }
}
