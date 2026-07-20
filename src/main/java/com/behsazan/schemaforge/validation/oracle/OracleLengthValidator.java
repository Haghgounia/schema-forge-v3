package com.behsazan.schemaforge.validation.oracle;

/** Validates Oracle identifier length limits. */
public final class OracleLengthValidator {

    public static final int MAX_IDENTIFIER_LENGTH = 128;

    public void requireValidIdentifierLength(String value, String objectType) {
        if (value == null || value.isBlank()) {
            return;
        }

        String normalized = value.trim();
        if (normalized.length() > MAX_IDENTIFIER_LENGTH) {
            throw new IllegalArgumentException(
                    "Oracle " + normalizedObjectType(objectType)
                            + " identifier '" + normalized + "' exceeds the "
                            + MAX_IDENTIFIER_LENGTH + " character limit"
            );
        }
    }

    private String normalizedObjectType(String objectType) {
        return objectType == null || objectType.isBlank() ? "object" : objectType.trim();
    }
}
